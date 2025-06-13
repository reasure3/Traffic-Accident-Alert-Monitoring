/**
 * Import function triggers from their respective submodules:
 *
 * import {onCall} from "firebase-functions/v2/https";
 * import {onDocumentWritten} from "firebase-functions/v2/firestore";
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

import { onCall, HttpsError } from "firebase-functions/v2/https";
import * as admin from "firebase-admin";

admin.initializeApp();

// Esta es una "Callable Function", lo que significa que la puedes llamar
// directamente desde tu app de Android de forma segura.
export const updateConfigAndLog = onCall(async (request) => {
    // 1. Comprobar que el usuario que llama está autenticado. Si no, la función falla.
    if (!request.auth) {
        throw new HttpsError(
            "unauthenticated",
            "The function must be called while authenticated.",
        );
    }
    
    // TODO OPCIONAL: Si tuvieras diferentes roles de usuario, aquí podrías verificar
    // si el usuario tiene permisos de administrador antes de continuar.
    // const userEmail = request.auth.token.email;
    // if (!userEmail || !userEmail.endsWith("@admin.example.com")) {
    //     throw new HttpsError(
    //         "permission-denied",
    //         "User is not an administrator.",
    //     );
    // }

    const remoteConfig = admin.remoteConfig();
    const db = admin.firestore();

    try {
        // 2. Obtener la plantilla actual de Remote Config para no sobreescribir otros valores.
        const template = await remoteConfig.getTemplate();

        // 3. Actualizar los valores de la plantilla con los datos recibidos desde la app.
        // La app enviará un objeto 'data' con los nuevos valores.
        const newConfig = request.data.newConfig; // ej: { "alert_radius_meters": 4500, ... }
        for (const key in newConfig) {
            if (Object.prototype.hasOwnProperty.call(newConfig, key)) {
                // Actualiza el valor por defecto del parámetro.
                template.parameters[key].defaultValue = { value: String(newConfig[key]) };
            }
        }

        // 4. Publicar (guardar) la plantilla modificada en Remote Config.
        await remoteConfig.publishTemplate(template);

        // 5. Guardar el registro del cambio en la colección 'config_history' de Firestore.
        await db.collection("config_history").add({
            timestamp: new Date(),
            adminUser: request.auth.token.email || "Unknown Admin",
            changes: newConfig, // Guardamos un mapa de los cambios realizados.
        });

        // 6. Devolver una respuesta de éxito a la app.
        return { success: true, message: "Configuration updated successfully!" };

    } catch (error) {
        console.error("Error updating remote config or logging history:", error);
        // Devolver una respuesta de error a la app.
        throw new HttpsError(
            "internal",
            "An error occurred while saving the configuration.",
        );
    }
});

export const getHistory = onCall(async (request) => {
    // 1. Comprobar que el usuario está autenticado.
    if (!request.auth) {
        throw new HttpsError("unauthenticated", "Authentication required.");
    }

    try {
        const db = admin.firestore();
        // 2. Acceder a la colección, ordenar por fecha (más nuevos primero) y limitar a 50 resultados.
        const snapshot = await db.collection("config_history")
            .orderBy("timestamp", "desc")
            .limit(50)
            .get();

        // 3. Mapear los documentos a un array de objetos y devolverlo.
        const historyRecords = snapshot.docs.map(doc => {
            const data = doc.data();
            return {
                ...data,
                // Convierte el Timestamp de Firestore a un formato estándar ISO para enviarlo al cliente.
                timestamp: data.timestamp.toDate().toISOString(),
            };
        });
        return historyRecords;

    } catch (error) {
        console.error("Error fetching history:", error);
        throw new HttpsError(
            "internal",
            "An error occurred while fetching the history.",
        );
    }
});
