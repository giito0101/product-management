# Product Management

Product Management は、商品在庫とユーザー権限を管理する Spring Boot 製の業務Webアプリケーションです。

商品登録・編集・削除、在庫状況の確認、ユーザー管理、CSV/Excelレポート出力、ログイン認証、操作ログ記録を行えます。

## 主な機能

- 商品管理: 商品一覧、検索、ページング、登録、編集、削除
- 在庫管理: 在庫切れ商品、残り少ない商品の抽出
- レポート出力: 在庫レポートのCSV/Excelダウンロード
- ユーザー管理: ユーザー一覧、検索、登録、編集、削除、ロール付与
- 認証・認可: Spring Securityによるログイン、Remember-Me、ロール別アクセス制御
- 操作ログ: ログイン、ログアウト、商品追加、商品更新、商品削除の記録

## アプリケーション構成

このアプリケーションは、画面を返す Spring MVC + Thymeleaf のWebアプリです。

明示的な `@RestController` はなく、JSON API中心の構成ではありません。ただし、CSV/Excelのファイルダウンロード用エンドポイントがあります。

- `GET /product/csv`
- `GET /product/excel`

基本的な処理の流れは次の通りです。

```text
Controller
  ↓
Service
  ↓
DAO / Repository / Mapper
  ↓
Database
```

Controllerはリクエストを受け取り、画面表示、リダイレクト、ファイル出力を担当します。

Serviceは検索、登録、更新、削除、在庫ステータス判定、重複チェック、ロール付与などの業務ロジックを担当します。

DAO / Repository / Mapperはデータベースアクセスを担当します。商品検索はJPA/JPQL、ユーザー検索はMyBatisを利用しています。

## 権限

- `EMPLOYEE`: ホーム画面、商品管理、在庫レポートにアクセス可能
- `MANAGER`: ユーザー管理にアクセス可能
- `ADMIN`: ユーザー管理にアクセス可能

## 主なデータ

- `users`: ログインユーザー
- `role`: 権限
- `users_roles`: ユーザーと権限の中間テーブル
- `product`: 商品
- `category`: 商品カテゴリ
- `stock_history`: 在庫変更履歴
- `log`: 操作ログ

## 技術スタック

- Java 21
- Spring Boot 3.4
- Spring MVC
- Thymeleaf
- Spring Security
- Spring Data JPA
- MyBatis
- MySQL
- H2 Database
- Apache POI
- Maven

## ローカル起動

ローカル環境では `local` プロファイルを指定して起動します。

```bash
SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run
```

ログイン確認には次のユーザーを使用できます。

```text
ユーザー名: tanaka
パスワード: password1
```

## データベース作成SQL

ローカルMySQL用のテーブル作成SQLは `sql_script/create.sql` に配置しています。

## テスト実行の事前準備

テストは Java 21 で実行します。Java 21 を使用するために、事前に次の環境変数を設定してください。

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@21
export PATH="$JAVA_HOME/bin:$PATH"
```

設定後、Java のバージョンを確認します。

```bash
java -version
```

`21` 系のバージョンが表示されれば準備完了です。

## テスト実行

`test` プロファイルを指定してテストを実行します。

```bash
SPRING_PROFILES_ACTIVE=test ./mvnw test
```

ビルドまで確認する場合は次のコマンドを実行します。

```bash
SPRING_PROFILES_ACTIVE=test ./mvnw package
```
