
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

/**
 * @program: ReceiveShowData
 * @author: zyd
 * @description: show the data about uv to the web
 * @create: 2018-07-02 16:14
 */

public class ShowUVSocketServer extends WebSocketServer {

    public ShowUVSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    public ShowUVSocketServer(InetSocketAddress address) {
        super(address);
    }

    private static final String FILE_PATH = "hdfs://master:9000/test";

    private static final int time = 1000 * 10;

    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println("open the WebsocketServer");
        Configuration conf = new Configuration();

        Path path = new Path(FILE_PATH);
        FileSystem fs = null;


        while (true) {
            StringBuffer buffer = new StringBuffer();
            FSDataInputStream fsr = null;
            BufferedReader bufferReader = null;
            String lineText = null;
            //文件目录
            FileStatus[] status = null;
            URI uri;
            try {
                uri = new URI(FILE_PATH);
                fs = FileSystem.newInstance(uri, conf);

                try {
                    while (!fs.exists(path)) {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                while (fs.listStatus(path) == null || fs.listStatus(path).length <= 0) {
                }
                status = fs.listStatus(path);

                //根据文件夹修改时间进行降序
                Arrays.sort(status, new Comparator<FileStatus>() {
                    public int compare(FileStatus o1, FileStatus o2) {
                        return o1.getModificationTime() > o2.getModificationTime() ? -1 : 1;
                    }
                });

                //读取文件夹下最新的数据文件夹
                Path logPath = status[0].getPath();
                System.out.println(logPath.toString());
                FileSystem logFs = FileSystem.newInstance(logPath.toUri(), conf);
                FileStatus[] logStatus = logFs.listStatus(logPath);
                if (logStatus != null) {
                    for (FileStatus file : logStatus) {
                        System.out.println(file.getPath().getName());
                        if (!file.getPath().getName().startsWith("part-")) {
                            continue;
                        } else {
                            FileSystem fileFs = FileSystem.newInstance(file.getPath().toUri(), conf);
                            fsr = fileFs.open(file.getPath());
                            bufferReader = new BufferedReader(new InputStreamReader(fsr));
                            if (null != (lineText = bufferReader.readLine())) {
                                System.out.println(lineText);
                                webSocket.send(lineText);
                            }
                        }
                    }
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println(reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println(conn.getLocalSocketAddress() + message);

    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        //错误时候触发的代码
        System.out.println("on error");
        ex.printStackTrace();
    }

    public void onStart() {
        System.out.println("server started successfully");
    }


    /**
     * @description: 实时返回uv给web
     */

    public void sendUVData(WebSocket web, String message) throws InterruptedException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < 100; i++) {
            Date date = new Date();
            web.send(dateFormat.format(date) + ";" + i);
            Thread.sleep(1000);
        }
    }

    public static void main(String[] args) throws Exception {
        WebSocketImpl.DEBUG = false;
        int port = 8887; // 端口
        ShowUVSocketServer s = new ShowUVSocketServer(port);
        s.start();
    }
}
