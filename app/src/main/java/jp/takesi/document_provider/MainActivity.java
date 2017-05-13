package jp.takesi.document_provider;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

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

        // そのACTION_OPEN_DOCUMENTはREAD_REQUEST_CODEとともにインテントが送信されました
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
                dumpImageMetaData(uri);
            }
        }
    }

    public void dumpImageMetaData(Uri uri) {

        // クエリは、単一のドキュメントにのみ適用されるため、1行だけを返します。
        // 1つのドキュメントのすべてのフィールドが必要なので、
        // フィールドのフィルタリング、ソート、または選択は不要です。
        Cursor cursor = this.getContentResolver()
                .query(uri, null, null, null, null, null);

        try {
            // カーソルが0行の場合、moveToFirst()はfalseを返します。
            // "見るべきことがあれば非常に便利です。それを見てください "条件文。
            if (cursor != null && cursor.moveToFirst()) {

                // 「表示名」と呼ばれることに注意してください。
                // これはプロバイダ固有であり、必ずしもファイル名である必要はありません。
                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.i("[important]", "Display Name: " + displayName);

                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                // サイズが不明の場合、格納される値はnullです。
                // しかし、Javaではintをnullにすることはできないため、
                // 振る舞いは実装固有のものであり、これは単に「予測不可能な」ための素晴らしい用語です。
                // したがって、原則として、intに代入する前にnullであるかどうかを確認してください。
                // これは頻繁に起こります。
                // ストレージAPIでは、ローカルにサイズがわからないリモートファイルが許可されます。
                String size = null;
                if (!cursor.isNull(sizeIndex)) {
                    // 技術的には列にintが格納されますが、
                    // cursor.getString()は変換を自動的に行います。
                    size = cursor.getString(sizeIndex);
                } else {
                    size = String.valueOf(R.string.unknown);
                }
                Log.i("[important]", "Size: " + size);
                TextView textView = (TextView)findViewById(R.id.textView);
                textView.setText("Display Name : " + displayName + "\n" + "Size : " + size + "\n" + "Uri : " + uri.toString());
            }
        } finally {
            cursor.close();
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
        if (id == R.id.action_about) {
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
            alertDialog.setTitle(R.string.action_about);//set title
            alertDialog.setMessage(R.string.about);//set content
            alertDialog.setIcon(R.mipmap.ic_launcher);//set icon
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.create();
            alertDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
