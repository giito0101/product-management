document.addEventListener("DOMContentLoaded", function () {
    // バッジ要素をすべて取得
    const badges = document.querySelectorAll(".badge");

    badges.forEach(badge => {
        const statusText = badge.textContent.trim(); // バッジのテキストを取得してトリム

        // クラスの変更
        if (statusText === "新規") {
            badge.classList.add("text-bg-info");
            badge.classList.remove("text-bg-success");
        } else if (statusText === "在庫無し") {
            badge.classList.add("text-bg-danger");
            badge.classList.remove("text-bg-success");
        } else if (statusText === "残り少ない") {
            badge.classList.add("text-bg-warning");
            badge.classList.remove("text-bg-success");
        } // "在庫有り" の場合は何もしない
    });
});