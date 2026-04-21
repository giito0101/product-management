// format_number.js
document.addEventListener("DOMContentLoaded", () => {
    const priceElements = document.querySelectorAll("[data-price]");

    priceElements.forEach((element) => {
        const price = parseFloat(element.textContent.trim());
        if (!isNaN(price)) {
            element.textContent = price.toLocaleString();
        }
    });
});
