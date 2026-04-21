document.addEventListener('DOMContentLoaded', function() {
    const fields = [
      // ユーザー名のバリデーション（2文字以上50文字以下）
      {
        id: 'validation01',
        parentClass: '.div01',
        validate: value => {
          const v = value.trim();
          return v !== '' && v.length >= 2 && v.length <= 50;
        }
      },
      // パスワードのバリデーション（空欄または8〜16文字）
      {
        id: 'validation02',
        parentClass: '.div02',
        validate: value => {
          const v = value.trim();
          return v !== '' && (v.length >= 8 && v.length <= 16);
        }
      },
      // 名字のバリデーション（1文字以上30文字以下）
      {
        id: 'validation03',
        parentClass: '.div03',
        validate: value => {
          const v = value.trim();
          return v !== '' && v.length >= 1 && v.length <= 30;
        }
      },
      // 名前のバリデーション（1文字以上30文字以下）
      {
        id: 'validation04',
        parentClass: '.div04',
        validate: value => {
          const v = value.trim();
          return v !== '' && v.length >= 1 && v.length <= 30;
        }
      },
      // 役職のバリデーション（空欄不可）
      {
        id: 'validation05',
        parentClass: '.div05',
        validate: value => value.trim() !== ''
      },
      // メールのバリデーション（3文字以上254文字以下）
      {
        id: 'validation06',
        parentClass: '.div06',
        validate: value => {
          const v = value.trim();
          return v !== '' && v.length >= 3 && v.length <= 254;
        }
      }
    ];

	// 各フィールドに初回バリデーションを適用
	fields.forEach(field => {
		const element = document.getElementById(field.id);
		if (!element) return; // 要素が存在しない場合はスキップ

		const isValid = field.validate(element.value);
		const parent = element.closest(field.parentClass);

		applyValidationResult(parent, element, isValid);
		console.log(`現在のクラス (${element.id}):`, element.classList.toString());
	});

	// 各フィールドにイベントを設定（変更時にバリデーションチェック）
	fields.forEach(field => {
		const element = document.getElementById(field.id);
		if (!element) return;

		element.addEventListener('change', function() {
			const isValid = field.validate(this.value);
			const parent = this.closest(field.parentClass);
			applyValidationResult(parent, this, isValid);
			console.log(`現在のクラス (${this.id}):`, this.classList.toString());
		});
	});

	// バリデーション結果をUIに反映する関数
	function applyValidationResult(parent, element, isValid) {
		const { valid: feedbackValid, invalid: feedbackInvalid } = getFeedbackElements(parent);

		if (isValid) {
			// validメッセージを表示
			feedbackValid.style.display = 'block';
			feedbackInvalid.style.display = 'none';

			// is-valid クラスを追加し、is-invalid クラスを削除
			element.classList.add('is-valid');
			element.classList.remove('is-invalid');
		} else {
			// validメッセージを非表示
			feedbackValid.style.display = 'none';
			feedbackInvalid.style.display = 'block';

			// is-invalid クラスを追加し、is-valid クラスを削除
			element.classList.add('is-invalid');
			element.classList.remove('is-valid');
		}
	}

	// フィードバック要素を取得する関数
	function getFeedbackElements(parent) {
		return {
			valid: parent.querySelector('.valid-feedback'),
			invalid: parent.querySelector('.invalid-feedback')
		};
	}
});