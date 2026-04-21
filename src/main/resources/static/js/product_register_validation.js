// ページロード時の初期バリデーションチェック
document.addEventListener('DOMContentLoaded', function() {
	const fields = [
		// 商品名のバリデーション
		{ id: 'validation01', parentClass: '.div01', validate: value => value.trim() !== '' && value.length >= 2 && value.length <= 100 },
		// カテゴリのバリデーション
		{ id: 'validation02', parentClass: '.div02', validate: value => value.trim() !== '' && value > 0 },
		// 在庫数のバリデーション
		{ id: 'validation03', parentClass: '.div03', validate: value => value.trim() !== '' && value >= 0 && value <= 1000000 },
		// 価格のバリデーション
		{ id: 'validation04', parentClass: '.div04', validate: value => value.trim() !== '' && value >= 10 && value <= 1000000000 }
	];

	// 各フィールドにイベントを設定
	fields.forEach(field => {
		const element = document.getElementById(field.id);
		const isValid = field.validate(element.value);
		const parent = element.closest(field.parentClass);

		const { valid: feedbackValid, invalid: feedbackInvalid } = getFeedbackElements(parent);

		if (isValid) {
			feedbackValid.style.display = 'block';
			feedbackInvalid.style.display = 'none';
			element.classList.add('is-valid');
			element.classList.remove('is-invalid');
		} else {
			feedbackValid.style.display = 'none';
			feedbackInvalid.style.display = 'block';
			element.classList.add('is-invalid');
			element.classList.remove('is-valid');
		}
		console.log(`現在のクラス (${element.id}):`, element.classList.toString());
	});

	// 属性を取得する
	function getFeedbackElements(parent) {
		return {
			valid: parent.querySelector('.valid-feedback'),
			invalid: parent.querySelector('.invalid-feedback')
		};
	}
});


// 商品名のバリデーション
// イベントリスナーを設定
document.getElementById('validation01').addEventListener('change', function() {
	const isValid = this.value.trim() !== '' && this.value.length >= 2 && this.value.length <= 100; // 条件をチェック
	const parent = this.closest('.div01');
	toggleValidationFeedback(parent, this, isValid); // 共通関数で処理
	console.log('現在のクラス (商品名):', this.classList.toString());
});

// カテゴリのバリデーション
document.getElementById('validation02').addEventListener('change', function() {
	const isValid = this.value.trim() !== '' && this.value >= 0; // 条件をチェック
	const parent = this.closest('.div02');
	toggleValidationFeedback(parent, this, isValid); // 共通関数で処理
	console.log('現在のクラス (カテゴリ):', this.classList.toString());
});

// 在庫数のバリデーション
document.getElementById('validation03').addEventListener('change', function() {
	const isValid = this.value.trim() !== '' && this.value >= 0 && this.value <= 1000000; // 条件をチェック
	const parent = this.closest('.div03');
	toggleValidationFeedback(parent, this, isValid); // 共通関数で処理
	console.log('現在のクラス (在庫数):', this.classList.toString());
});

// 価格のバリデーション
document.getElementById('validation04').addEventListener('change', function() {
	const isValid = this.value.trim() !== '' && this.value >= 10 && this.value <= 1000000000; // 条件をチェック
	const parent = this.closest('.div04'); // 親要素を取得
	toggleValidationFeedback(parent, this, isValid); // 共通関数で処理
	console.log('現在のクラス (価格):', this.classList.toString());
});

// フィードバック表示/非表示とクラス操作を処理する関数を定義
function toggleValidationFeedback(parent, element, isValid) {
	const { valid: feedbackValid, invalid: feedbackInvalid } = getFeedbackElements(parent);

	if (isValid) {
		// validメッセージを表示
		feedbackValid.style.display = 'block';
		// invalidメッセージを非表示
		feedbackInvalid.style.display = 'none';

		// elementにis-validクラスを追加し、is-invalidクラスを削除
		element.classList.add('is-valid');
		element.classList.remove('is-invalid');
	} else {
		// validメッセージを非表示
		feedbackValid.style.display = 'none';
		// invalidメッセージを表示
		feedbackInvalid.style.display = 'block';

		// elementにis-invalidクラスを追加し、is-validクラスを削除
		element.classList.add('is-invalid');
		element.classList.remove('is-valid');
	}
}

// FeedbackElementsを取得
function getFeedbackElements(parent) {
	return {
		valid: parent.querySelector('.valid-feedback'),
		invalid: parent.querySelector('.invalid-feedback')
	};
}