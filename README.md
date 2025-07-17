# 書籍・著者管理API（クオカード社コーディングテスト）

Spring BootとKotlin、jOOQを使用した書籍・著者管理アプリケーションです。

* **参考URL:** https://quo-digital.hatenablog.com/entry/2024/03/22/143542

---

## システム要件

* 技術要件
    * 言語: Kotlin
    * フレームワーク: Spring Boot、jOOQ
* 必要な機能
    * 書籍と著者の情報をRDBに登録・更新できる機能
    * 著者に紐づく本を取得できる機能
* 書籍の属性
    * タイトル
    * 価格（0以上であること）
    * 著者（最低1人の著者を持つ。複数の著者を持つことが可能）
    * 出版状況（未出版、出版済み。出版済みステータスのものを未出版には変更できない）
* 著者の属性
    * 名前
    * 生年月日（現在の日付よりも過去であること）
    * 著者も複数の書籍を執筆できる

---

## 環境構築と実行方法

1. データベースの起動  
   プロジェクトのルートディレクトリで以下のコマンドを実行し、PostgreSQLコンテナをバックグラウンドで起動します。

    ```bash
    docker-compose up -d
    ```

1. Flywayマイグレーションの実行 (初回のみ)  
   以下のコマンドでデータベースのテーブルを自動で作成します。

    ```bash
    ./gradlew flywayMigrate
    ```

1. アプリケーションの起動  
   以下のコマンドでSpring Bootアプリケーションを起動します。

    ```bash
    ./gradlew bootRun
    ```

   アプリケーションは http://localhost:8080 で起動します。

---

## API仕様書 (OpenAPI)

アプリケーションの起動後、以下のURLにアクセスすることで、API仕様書（Swagger UI）を確認できます。

* Swagger UI
    * http://localhost:8080/swagger-ui.html

各APIエンドポイントの詳細な仕様確認や、その場でのリクエスト送信が可能です。

---

## テストの実行

以下のコマンドでテストを実行します。

   ```bash
   ./gradlew test
   ```

テストが完了すると、build/reports/tests/test/index.html に詳細なテストレポートが生成されます。
