import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @program: ReceiveShowData
 * @author: zyd
 * @description: receive the data about uv from hdfs
 * @create: 2018-07-02 16:50
 */
public class ReceiveUVSocketClient extends WebSocketClient {

    public ReceiveUVSocketClient(URI serverUri) {
        super(serverUri);
    }

    public ReceiveUVSocketClient(URI serverUri, Draft protocolDraft) {
        super(serverUri, protocolDraft);
    }

    private static final String FILE_PATH = "";

    public void onOpen(ServerHandshake serverHandshake) {
        Configuration conf = new Configuration();

        Path path = new Path(FILE_PATH);
        FileSystem fs = null;
        try {
            while (!fs.exists(path)) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuffer buffer = new StringBuffer();
        FSDataInputStream fsr = null;
        BufferedReader bufferReader = null;
        String lineText = null;
        //文件目录
        FileStatus[] status = null;
        URI uri;
        try {
            uri = new URI(FILE_PATH);
            fs = FileSystem.get(uri, conf);

            fsr = fs.open(path);

            status = fs.listStatus(path);
            while (status == null) {
            }



            while ((lineText = bufferReader.readLine()) != null) {
//                sendUVData(webSocket, lineText);
                Thread.sleep(1000 * 30);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(fsr);
        }
        send("uv;127;12");
        System.out.println("new connection opened");
    }

    public void onMessage(String s) {
        System.out.println("received message: " + s);
    }

    public void onClose(int i, String s, boolean b) {
        System.out.println("closed with exit code " + i + " additional info: " + s);
    }

    public void onError(Exception e) {
        System.err.println("an error occurred:" + e);
    }

    public static void main(String[] args) throws URISyntaxException {
        WebSocketClient client = new ReceiveUVSocketClient(new URI("ws://localhost:8887"));
        client.connect();
    }
}
