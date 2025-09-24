document.addEventListener("DOMContentLoaded", async () => {
    const stripe = Stripe(publicKey);

    const elements = stripe.elements();
    const card = elements.create("card");
    card.mount("#card-element");

    const form = document.getElementById("payment-form");
    const messageDiv = document.getElementById("payment-message");

    form.addEventListener("submit", async (e) => {
        e.preventDefault();
        messageDiv.textContent = "Processing...";

        try {
            // Step 1: Tell backend to prepare Stripe PaymentIntent
            await fetch(`/api/payments/${paymentId}/start`, { method: "POST" });

            // Step 2: Fetch payment intent from backend (your actual app should probably return client_secret)
            const res = await fetch(`/api/internal/payment-intent-secret/${paymentId}`);
            const { clientSecret } = await res.json();

            // Step 3: Confirm payment with Stripe.js (card + potential 3D Secure)
            const result = await stripe.confirmCardPayment(clientSecret, {
                payment_method: {
                    card: card,
                },
            });

            if (result.error) {
                messageDiv.textContent = `❌ ${result.error.message}`;
            } else if (result.paymentIntent.status === "succeeded") {
                // Optionally notify backend
                await fetch(`/api/payments/intent/${result.paymentIntent.id}/confirm`, { method: "POST" });

                messageDiv.textContent = "✅ Payment successful!";
            } else {
                messageDiv.textContent = "⚠️ Payment status: " + result.paymentIntent.status;
            }
        } catch (err) {
            messageDiv.textContent = `💥 Error: ${err.message}`;
        }
    });
});
