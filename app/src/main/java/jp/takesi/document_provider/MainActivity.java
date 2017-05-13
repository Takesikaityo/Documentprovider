package jp.takesi.document_provider;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 42;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ACTION_OPEN_DOCUMENTはシステムのファイルを介してファイルを選択するIntentです
                // ブラウザ
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                // フィルタ（連絡先やタイムゾーンのリストではなく）ファイルなど、
                // 「開く」ことができる結果のみが表示されます。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                // 画像のMIMEデータタイプを使用して、画像のみを表示するフィルタ。
                // ogg vorbisファイルを検索する場合、そのタイプは "audio/ogg" になります。
                // インストールされたストレージプロバイダを介して利用可能なすべてのドキュメントを検索するには、
                // それはそのようになります "*/*"
                intent.setType("image/*");
                startActivityForResult(intent, READ_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // そのACTION_OPEN_DOCUMENTはリクエストコードとともにインテントが送信されました
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // ここに表示されているリクエストコードが一致しない場合は、他の意図に対する応答です。
        // 下のコードは実行しないでください。

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // ユーザーが選択したドキュメントは、Intentで返されません。
            // 代わりに、その文書へのURIがreturnインテントに含まれます
            // このメソッドにパラメータとして渡されます。
            // resultData.getData()を使用してそのURIを取得します。
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i("[Important]", "Uri: " + uri.toString());
                ImageView imageview = (ImageView) findViewById(R.id.imageView);
                imageview.setImageURI(uri);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // メニューを膨らませる。 アクションバーが存在する場合、アクションバーに項目が追加されます。
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // アクションバーのアイテムのハンドルをここで処理します。
        // アクションバーは、AndroidManifest.xmlで親アクティビティを指定する限り、
        // Home/Upボタンのクリックを自動的に処理します。
        int id = item.getItemId();

        //簡単な説明
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
