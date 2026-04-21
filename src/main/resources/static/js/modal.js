document.addEventListener('DOMContentLoaded', function() {
	const deleteLinks = document.querySelectorAll('.delete-link');
	const confirmDeleteModal = new bootstrap.Modal(document.getElementById('confirmDeleteModal'));
	const confirmDeleteButton = document.getElementById('confirmDeleteButton');

	deleteLinks.forEach(link => {
		link.addEventListener('click', function(event) {
			event.preventDefault(); // デフォルトのリンク動作を無効化
            const href = link.getAttribute('href'); // href 属性の値を取得
            console.log(href); // コンソールで確認
            confirmDeleteButton.setAttribute('href', href); // モーダル内のボタンにURLを設定
			confirmDeleteModal.show(); // モーダルを表示
		});
	});
});