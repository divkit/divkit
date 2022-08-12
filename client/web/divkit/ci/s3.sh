set -ex

aws --endpoint-url=http://s3.mds.yandex.net s3 cp --recursive \
  "dist/browser/" \
  "s3://$BUCKET/$VERSION" \
  --exclude '*' \
  --include '*.css' \
  --include '*.js'

aws --endpoint-url=http://s3.mds.yandex.net s3 cp --recursive \
  "dist/browser/" \
  "s3://$BUCKET/$VERSION" \
  --exclude '*' \
  --include '*.css.gz' \
  --include '*.css.br' \
  --no-guess-mime-type \
  --content-type="text/css"

aws --endpoint-url=http://s3.mds.yandex.net s3 cp --recursive \
  "dist/browser/" \
  "s3://$BUCKET/$VERSION" \
  --exclude '*' \
  --include '*.js.gz' \
  --include '*.js.br' \
  --no-guess-mime-type \
  --content-type="application/javascript"

aws --endpoint-url=http://s3.mds.yandex.net s3 cp --recursive \
  "dist/browser/" \
  "s3://$BUCKET/$VERSION" \
  --exclude '*' \
  --include '*.map' \
  --no-guess-mime-type \
  --content-type="application/json"

aws --endpoint-url=http://s3.mds.yandex.net s3 cp \
  "dist/client.css" "s3://$BUCKET/$VERSION/client.css"
aws --endpoint-url=http://s3.mds.yandex.net s3 cp \
  "dist/client.css.gz" "s3://$BUCKET/$VERSION/client.css.gz" --content-type="text/css"
aws --endpoint-url=http://s3.mds.yandex.net s3 cp \
  "dist/client.css.br" "s3://$BUCKET/$VERSION/client.css.br" --content-type="text/css"
aws --endpoint-url=http://s3.mds.yandex.net s3 cp \
  "dist/client.css.map" "s3://$BUCKET/$VERSION/client.css.map" --content-type="application/json"
