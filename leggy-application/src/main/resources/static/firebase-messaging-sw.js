importScripts('https://www.gstatic.com/firebasejs/9.22.1/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/9.22.1/firebase-messaging-compat.js');

firebase.initializeApp({
    // For Firebase JS SDK v7.20.0 and later, measurementId is optional
        apiKey: "AIzaSyCGI553X9W_Y73PQmmXNFDpUKRyjeAHsNY",
        authDomain: "legy-79c27.firebaseapp.com",
        projectId: "legy-79c27",
        storageBucket: "legy-79c27.firebasestorage.app",
        messagingSenderId: "540049131134",
        appId: "1:540049131134:web:221a2bdca704cefe317a2f",
        measurementId: "G-9FW6DSH9LL"
});

const messaging = firebase.messaging();

messaging.onBackgroundMessage(function(payload) {
    console.log('[firebase-messaging-sw.js] Received background message ', payload);

    const notificationTitle = payload.notification.title;
    const notificationOptions = {
        body: payload.notification.body,
        icon: '/logo192.png' // adapte si tu as une icône
    };

    self.registration.showNotification(notificationTitle, notificationOptions);

});
messaging.onMessage(payload => {
    console.log('Foreground message ', payload);
    new Notification(payload.notification.title, { body: payload.notification.body });
});