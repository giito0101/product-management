# Product Management

## ローカル起動

ローカル環境では `local` プロファイルを指定して起動します。

```bash
SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run
```

## データベース作成SQL

ローカルMySQL用のテーブル作成SQLは `sql_script/create.sql` に配置しています。

## テスト実行

テストは Java 21 と `test` プロファイルを指定して実行します。

```bash
JAVA_HOME=/opt/homebrew/opt/openjdk@21 PATH=/opt/homebrew/opt/openjdk@21/bin:$PATH SPRING_PROFILES_ACTIVE=test ./mvnw test
```
