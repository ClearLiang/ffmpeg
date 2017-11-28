import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Main {
    private final static String VIDEOPATH = "G:\\ffmpeg\\input\\123.mp4";
    static Date date = new Date();
    static String time = String.valueOf(date.getTime());

    private final static String StringValue = "ffmpeg -re -i \"D:\\download\\film\\aqgy\\02.mp4\" -vcodec libx264 -vprofile baseline -acodec aac  \n" +
            " -ar 44100 -strict -2 -ac 1 -f flv -s 1280x720 -q 10 rtmp://server:1935/  \n" +
            "myapp/test1  ";

    public static void main(String[] args) {
        System.out.println("time时间是："+time);
        if (!checkfile(VIDEOPATH)) {   //判断路径是不是一个文件
            System.out.println(VIDEOPATH + " is not file");
            return;
        }
        if (process()) {        //执行转码任务
            System.out.println("ok");
        }
    }

    private static boolean checkfile(String path) {
        File file = new File(path);
        if (!file.isFile()) {
            return false;
        }
        return true;
    }

    private static boolean process() {
        // 判断视频的类型
        int type = checkContentType();
        System.out.println("类型是："+type);
        boolean status = false;
        //如果是ffmpeg可以转换的类型直接转码，否则先用mencoder转码成AVI
        if (type == 0) {
            System.out.println("直接将文件转为flv文件");
            status = processFLV(VIDEOPATH);// 直接将文件转为flv文件
        } else if (type == 1) {
            String avifilepath = processAVI(type);
            if (avifilepath == null)
                return false;// avi文件没有得到
            status = processFLV(avifilepath);// 将avi转为flv
        }
        return status;
    }

    private static int checkContentType() {
        String type = VIDEOPATH.substring(VIDEOPATH.lastIndexOf(".") + 1, VIDEOPATH.length())
                .toLowerCase();
        // ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）
        if (type.equals("avi")) {
            return 0;
        } else if (type.equals("mpg")) {
            return 0;
        } else if (type.equals("wmv")) {
            return 0;
        } else if (type.equals("3gp")) {
            return 0;
        } else if (type.equals("mov")) {
            return 0;
        } else if (type.equals("mp4")) {
            return 0;
        } else if (type.equals("asf")) {
            return 0;
        } else if (type.equals("asx")) {
            return 0;
        } else if (type.equals("flv")) {
            return 0;
        }
        // 对ffmpeg无法解析的文件格式(wmv9，rm，rmvb等),
        // 可以先用别的工具（mencoder）转换为avi(ffmpeg能解析的)格式.
        else if (type.equals("wmv9")) {
            return 1;
        } else if (type.equals("rm")) {
            return 1;
        } else if (type.equals("rmvb")) {
            return 1;
        }
        return 9;
    }


    // 对ffmpeg无法解析的文件格式(wmv9，rm，rmvb等), 可以先用别的工具（mencoder）转换为avi(ffmpeg能解析的)格式.
    private static String processAVI(int type) {
        List<String> commend = new ArrayList<String>();
        commend.add("G:\\ffmpeg\\mencoder");
        commend.add(VIDEOPATH);
        commend.add("-oac");
        commend.add("lavc");
        commend.add("-lavcopts");
        commend.add("acodec=mp3:abitrate=64");
        commend.add("-ovc");
        commend.add("xvid");
        commend.add("-xvidencopts");
        commend.add("bitrate=600");
        commend.add("-of");
        commend.add("avi");
        commend.add("-o");
        commend.add("G:\\ffmpeg\\input\\"+time+".avi");
        try {
            //调用线程命令启动转码
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commend);
            builder.start();
            return "G:\\ffmpeg\\input\\001.avi";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）
    private static boolean processFLV(String oldfilepath) {
        if (!checkfile(VIDEOPATH)) {
            System.out.println(oldfilepath + " is not file");
            return false;
        }
        // 文件命名
        Calendar c = Calendar.getInstance();
        String savename = String.valueOf(c.getTimeInMillis())+ Math.round(Math.random() * 100000);
        List<String> commend = new ArrayList<String>();
        /*
        * ffmpeg -i C:\Users\dhht\Desktop\123.mp4 -ab 56 -ar 22050 -b 500 -r 15 -s 320x240 g:\test.flv
        * */
        commend.add("g:\\ffmpeg\\ffmpeg");          //文件名
        commend.add("-i");                          //指定输入文件
        commend.add(oldfilepath);
        commend.add("-ab");                         //设置音频码率
        commend.add("56");
        commend.add("-ar");                         //设置音频采样率
        commend.add("22050");
        commend.add("-b");                          //设置比特率
        commend.add("500");
        commend.add("-r");                          //设置帧频
        commend.add("15");
        commend.add("-s");                          //指定分辨率
        commend.add("600x500");
        commend.add("rtmp://10.0.0.196:1935/myapp/test1");
        //commend.add("g:\\ffmpeg\\output\\a.flv");   //指定输出文件

        try {
            Runtime runtime = Runtime.getRuntime();
            Process proce = null;
            //视频截图命令，封面图。  8是代表第8秒的时候截图
            String cmd = "";
            String cut = "     G:\\ffmpeg\\ffmpeg.exe   -i   "
                    + oldfilepath
                    + "   -y   -f   image2   -ss   2   -t   0.001   -s   600x500   G:\\ffmpeg\\output\\"
                    + "a.jpg";
            String cutCmd = cmd + cut;
            proce = runtime.exec(cutCmd);
            //调用线程命令进行转码
            ProcessBuilder builder = new ProcessBuilder(commend);
            builder.command(commend);
            builder.start();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
