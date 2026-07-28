# 本番デプロイ手順（EC2: i-026cff97b0d1138e1 / OnoyuWebServer, Amazon Linux 2023）

対象ドメイン: https://tokyo-hack-group.com/ （DNS設定済み・EC2のパブリックIPを指している前提）

## 0. 前提

- セキュリティグループで **80/443** がインターネットに開放されていること（22は自分のIPのみに制限推奨）
- EC2にSSH接続できること（EC2 Instance Connect または鍵ペア）

---

## 1. EC2への接続

```bash
# EC2 Instance Connect を使う場合（マネジメントコンソールから「接続」でも可）
aws ec2-instance-connect ssh --instance-id i-026cff97b0d1138e1
```

## 2. 必要パッケージのインストール（EC2上で実行）

```bash
sudo dnf update -y
sudo dnf install -y java-21-amazon-corretto nginx git
sudo systemctl enable --now nginx

# certbot（Let's Encrypt）
sudo dnf install -y python3-pip
sudo pip3 install certbot certbot-nginx
```

> `docker`のインストールはスキップ可能です。このサーバーには既にネイティブPostgreSQLが `127.0.0.1:5432` で稼働していたため（他アプリと共用中の可能性があるインスタンスでした）、Dockerコンテナは使わず既存PostgreSQL内に新しいDB/ユーザーを追加する方式に変更しています（手順3参照）。まっさらなEC2から始める場合はDocker版に戻しても構いません。

## 3. PostgreSQLのDB/ユーザー作成（既存のネイティブPostgreSQLを使用）

このEC2には既にPostgreSQL（システムサービス, `/var/lib/pgsql/data`）が稼働している前提です。既存のDB/ユーザーには触れず、新しいDBとユーザーだけを追加します。

```bash
sudo -u postgres psql -c "CREATE USER thg_app WITH PASSWORD '<強力なパスワードに置き換え>';"
sudo -u postgres psql -c "CREATE DATABASE thg_portal OWNER thg_app;"
```

`pg_hba.conf`（`sudo -u postgres psql -t -c "SHOW hba_file;"` で場所を確認）に
`host all all 127.0.0.1/32 scram-sha-256`（または同等の行）が既にあれば追加設定は不要です。接続確認:

```bash
PGPASSWORD='<上で設定したパスワード>' psql -h 127.0.0.1 -U thg_app -d thg_portal -c '\conninfo'
```

> まっさらなEC2で、ネイティブPostgreSQLが存在しない場合は、代わりにDockerで起動しても構いません:
> ```bash
> sudo dnf install -y docker && sudo systemctl enable --now docker
> sudo mkdir -p /opt/thg-portal/pgdata
> sudo docker run -d --name thg-portal-db --restart unless-stopped \
>   -e POSTGRES_DB=thg_portal -e POSTGRES_USER=thg_app \
>   -e POSTGRES_PASSWORD='<強力なパスワードに置き換え>' \
>   -v /opt/thg-portal/pgdata:/var/lib/postgresql/data \
>   -p 127.0.0.1:5432:5432 postgres:16
> ```

## 4. アプリ用ユーザー・ディレクトリの準備

```bash
sudo useradd -r -s /sbin/nologin thgportal || true
sudo mkdir -p /opt/thg-portal/uploads
sudo chown -R thgportal:thgportal /opt/thg-portal
```

## 5. メール送信設定（Amazon SES）

Google Workspaceアカウントではアプリパスワードが組織ポリシーで発行できないケースが多いため、AWSに合わせてAmazon SESを使用します。

1. **AWSコンソール → SES（リージョン: アジアパシフィック・東京 ap-northeast-1）→ Verified identities → Create identity**
   - 種類: Domain、ドメイン名: `tokyo-hack-group.com`、「Use a custom MAIL FROM domain」は任意、Easy DKIMを有効化
   - 表示されるDKIM用CNAMEレコード（3件）をRoute53（DNS管理側）に追加する
   - ステータスが `Verified` になるまで待つ（通常数分〜）
2. **本番送信の許可申請（サンドボックス解除）**
   - SESコンソール → Account dashboard → 「Request production access」から利用目的を記入して申請（内部グループ向け通知メール、など）。承認まで数時間〜1日程度かかることがあります。承認されるまでは検証済みのメールアドレス宛にしか送信できません。
3. **SMTP認証情報の発行**
   - SESコンソール → SMTP settings → 「Create SMTP credentials」→ IAMユーザーが作成され、SMTPユーザー名・パスワードが発行されます（この画面でのみ表示されるので必ず控える）

```bash
sudo tee /opt/thg-portal/env > /dev/null <<'EOF'
DB_URL=jdbc:postgresql://127.0.0.1:5432/thg_portal
DB_USER=thg_app
DB_PASSWORD=<手順3で設定したパスワードと同じ値>
MAIL_HOST=email-smtp.ap-northeast-1.amazonaws.com
MAIL_PORT=587
MAIL_USERNAME=<SESのSMTPユーザー名>
MAIL_PASSWORD=<SESのSMTPパスワード>
MAIL_SENDER=noreply@tokyo-hack-group.com
MAIL_ADMIN=admin@tokyo-hack-group.com
UPLOAD_DIR=/opt/thg-portal/uploads
EOF
sudo chown thgportal:thgportal /opt/thg-portal/env
sudo chmod 600 /opt/thg-portal/env
```

> `MAIL_SENDER`/`MAIL_ADMIN` は検証済みドメイン（`tokyo-hack-group.com`）配下のアドレスにしてください。未検証ドメインのアドレスから送信しようとするとSESに拒否されます。

## 6. ローカルでのビルド（開発PC側で実行）

```bash
cd tokyohackgroup-portal
./mvnw clean package -DskipTests
# target/tokyohackgroup-portal-0.0.1-SNAPSHOT.war が生成される（Spring Boot実行可能WAR、tomcat-embed-jasperでJSPを扱うためwarパッケージング）
```

## 7. warとsystemd unitをEC2へ転送

```bash
scp target/tokyohackgroup-portal-0.0.1-SNAPSHOT.war ec2-user@<EC2のIP>:/tmp/app.war
scp deploy/thg-portal.service ec2-user@<EC2のIP>:/tmp/thg-portal.service
scp deploy/nginx-thg-portal.conf ec2-user@<EC2のIP>:/tmp/nginx-thg-portal.conf
```

## 8. EC2上での配置・起動

```bash
sudo mv /tmp/app.war /opt/thg-portal/app.war
sudo chown thgportal:thgportal /opt/thg-portal/app.war

sudo mv /tmp/thg-portal.service /etc/systemd/system/thg-portal.service
sudo systemctl daemon-reload
sudo systemctl enable --now thg-portal
sudo systemctl status thg-portal   # active (running) を確認

sudo mv /tmp/nginx-thg-portal.conf /etc/nginx/conf.d/thg-portal.conf
sudo nginx -t
sudo systemctl reload nginx
```

この時点で `http://tokyo-hack-group.com/` にアクセスできることを確認してください。

## 9. HTTPS化（Let's Encrypt）

```bash
sudo certbot --nginx -d tokyo-hack-group.com -d www.tokyo-hack-group.com
# メールアドレス入力・利用規約同意の後、自動でnginx設定にTLSが追記されます
```

証明書は90日ごとの自動更新がcertbotのcronタイマーで設定されます（`sudo systemctl status certbot-renew.timer` で確認可能）。

## 10. 動作確認チェックリスト

- [ ] `https://tokyo-hack-group.com/` にHTTPSでアクセスできる
- [ ] ログイン・プロジェクト一覧・プロジェクト詳細が表示される
- [ ] ファイルアップロード（ドキュメント・アバター・アイコン）が保存・表示される
- [ ] お知らせ投稿でメール通知が届く（迷惑メールフォルダも確認）
- [ ] `sudo systemctl restart thg-portal` 後もアップロード済みファイルが消えない（`/opt/thg-portal/uploads` がEBS上にあるため永続化される）

## 11. 次回以降のデプロイ（更新時）

```bash
# ローカル
./mvnw clean package -DskipTests
scp target/tokyohackgroup-portal-0.0.1-SNAPSHOT.war ec2-user@<EC2のIP>:/tmp/app.war

# EC2
sudo systemctl stop thg-portal
sudo mv /tmp/app.war /opt/thg-portal/app.war
sudo chown thgportal:thgportal /opt/thg-portal/app.war
sudo systemctl start thg-portal
```

## 既知の注意点

- `spring.jpa.hibernate.ddl-auto=update` を使用しているため、エンティティの変更は自動でテーブルに反映されますが、本格運用が始まったらFlyway等のマイグレーション管理への切替を推奨します。
- DBパスワード・SESのSMTP認証情報は `/opt/thg-portal/env`（権限600、rootとthgportalのみ読み取り可）にのみ保存し、Gitには絶対にコミットしないでください。
- PostgreSQLは `127.0.0.1:5432` のみでリッスンしており、外部からは接続できません（ネイティブ稼働の場合も、Dockerコンテナの場合も同様）。
- SESがサンドボックスモードのままだと、検証済みのメールアドレス宛にしか送信できません。本番運用前に「Request production access」の承認を必ず確認してください。
