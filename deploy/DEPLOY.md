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
sudo dnf install -y java-21-amazon-corretto docker nginx git
sudo systemctl enable --now docker
sudo systemctl enable --now nginx

# certbot（Let's Encrypt）
sudo dnf install -y python3-pip
sudo pip3 install certbot certbot-nginx
```

## 3. PostgreSQLコンテナの起動

```bash
sudo mkdir -p /opt/thg-portal/pgdata
sudo docker run -d --name thg-portal-db \
  --restart unless-stopped \
  -e POSTGRES_DB=thg_portal \
  -e POSTGRES_USER=thg_app \
  -e POSTGRES_PASSWORD='<強力なパスワードに置き換え>' \
  -v /opt/thg-portal/pgdata:/var/lib/postgresql/data \
  -p 127.0.0.1:5432:5432 \
  postgres:16
```

## 4. アプリ用ユーザー・ディレクトリの準備

```bash
sudo useradd -r -s /sbin/nologin thgportal || true
sudo mkdir -p /opt/thg-portal/uploads
sudo chown -R thgportal:thgportal /opt/thg-portal
```

## 5. 環境変数ファイルの作成

```bash
sudo tee /opt/thg-portal/env > /dev/null <<'EOF'
DB_URL=jdbc:postgresql://127.0.0.1:5432/thg_portal
DB_USER=thg_app
DB_PASSWORD=<手順3で設定したパスワードと同じ値>
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=<送信用Gmailアドレス>
MAIL_PASSWORD=<Gmailアプリパスワード（16桁）※通常のログインパスワードではない>
UPLOAD_DIR=/opt/thg-portal/uploads
EOF
sudo chown thgportal:thgportal /opt/thg-portal/env
sudo chmod 600 /opt/thg-portal/env
```

> Gmailアプリパスワードは https://myaccount.google.com/apppasswords で発行してください（2段階認証が有効なアカウントが必要です）。

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
- DBパスワード・メールアプリパスワードは `/opt/thg-portal/env`（権限600、rootとthgportalのみ読み取り可）にのみ保存し、Gitには絶対にコミットしないでください。
- PostgreSQLコンテナは `127.0.0.1:5432` のみでリッスンしており、外部からは接続できません。
