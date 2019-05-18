package com.cn.tianxia.api.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream; 
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder; 
import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;  

public class IP {

    public static String randomIp() {
        Random r = new Random();
        StringBuffer str = new StringBuffer();
        str.append(r.nextInt(1000000) % 255);
        str.append(".");
        str.append(r.nextInt(1000000) % 255);
        str.append(".");
        str.append(r.nextInt(1000000) % 255);
        str.append(".");
        str.append(0);

        return str.toString();
    }
    
    public static void AllIp() {
    	 try {
 			IP.load("C:\\Users\\PC1\\Desktop\\IPAddress.dat",false);
 		} catch (Exception e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
        
        int a=1;
        int b=0;
        int c=0;
        int d=0;
        String ip="";
        String path="D:\\";
		String fileName="iplist.txt";
		
		//判断路径存不存在，不在则生成文件夹
		File file=new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
		
		path=path+"/"+fileName;
		file=new File(path);
		
		BufferedOutputStream  bufferOut=null; 
		try {
			//bufferOut=new BufferedOutputStream (new FileOutputStream(file)); 
			int i =0;
        while(i<100000000){
        	i++;
        	StringBuffer s=new StringBuffer();
        	if(d<255){
        		d++;
        	}
        	if(d>=255&&c<255){
        		if(d==255){
        			d=1;
        		}
        		c++;
        	}
        	if(c>=255&&b<255){
        		if(c==255){
        			c=1;
        		}
        		b++;
        	}
        	if(b>=255&&a<255){
        		if(b==255){
        			b=1;
        		}
        		a++;
        	}
        	s.append(a+"."+b+"."+c+"."+d);
        	ip=s.toString();
        	String ipads[]=IP.find(ip);
        	StringBuffer s1=new StringBuffer();
        	 for(int j=1;j<ipads.length;j++){
             	if(ipads[j]!=null&&!"".equals(ipads[j])){
             		if(j>1){
             			s1.append(",");
             		}
             		s1.append(ipads[j]);
             	}
             }
        	String address=s1.toString();
        	//System.out.println(ip+":"+address);
        	//写入ip
			//bufferOut.write((ip+":"+address).getBytes());
			//bufferOut.write("\r\n".getBytes());  
        }
		 } catch (Exception e) { 
				e.printStackTrace();
		}  
    }

    public static void main(String[] args){
     /*   try {
			IP.load("C:\\Users\\PC1\\Desktop\\IPAddress.dat",false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        String path="D:\\";
		String fileName="iplist.txt";
		
		//判断路径存不存在，不在则生成文件夹
		File file=new File(path);
		if(!file.exists()){
			file.mkdirs();
		}
		
		path=path+"/"+fileName;
		file=new File(path);
		
		BufferedOutputStream  bufferOut=null; 
		try {
			bufferOut=new BufferedOutputStream (new FileOutputStream(file));  
        Long st = System.nanoTime();
        for (int i = 0; i < 100000000; i++)
        {
            //IP.find(randomIp());
        	String ip=randomIp();
            String ipads[]=IP.find(ip);
        	StringBuffer s=new StringBuffer();
        	 for(int j=1;j<ipads.length;j++){
             	if(ipads[j]!=null&&!"".equals(ipads[j])){
             		if(j>1){
             			s.append(",");
             		}
             		s.append(ipads[j]);
             	}
             }
        	String address=s.toString();
        	//System.out.println(address);
        	
        	//写入ip
			bufferOut.write((ip+":"+address).getBytes());
			bufferOut.write("\r\n".getBytes()); 
        }
        bufferOut.flush();
        bufferOut.close();
        Long et = System.nanoTime();
        //System.out.println((et - st) / 1000 / 1000);
		} catch (Exception e) { 
			e.printStackTrace();
		}  */
    	AllIp();
    }

    public static boolean enableFileWatch = false;

    private static int offset;
    private static int[] index = new int[256];
    private static ByteBuffer dataBuffer;
    private static ByteBuffer indexBuffer;
    private static Long lastModifyTime = 0L;
    private static File ipFile ;
    private static ReentrantLock lock = new ReentrantLock();

    public static void load(String filename) {
    	String filePath = IP.class.getClassLoader().getResource(filename).getPath();
    	File file = new File(filePath);
        ipFile =file;
        load();
        if (enableFileWatch) {
            watch();
        }
    }

    public static void load(String filename, boolean strict) throws Exception {
        ipFile = new File(filename);
        if (strict) {
            int contentLength = Long.valueOf(ipFile.length()).intValue();
            if (contentLength < 512 * 1024) {
                throw new Exception("ip data file error.");
            }
        }
        load();
        if (enableFileWatch) {
            watch();
        }
    }

    public static String[] find(String ip) {
        int ip_prefix_value = new Integer(ip.substring(0, ip.indexOf(".")));
        long ip2long_value  = ip2long(ip);
        int start = index[ip_prefix_value];
        int max_comp_len = offset - 1028;
        long index_offset = -1;
        int index_length = -1;
        byte b = 0;
        for (start = start * 8 + 1024; start < max_comp_len; start += 8) {
            if (int2long(indexBuffer.getInt(start)) >= ip2long_value) {
                index_offset = bytesToLong(b, indexBuffer.get(start + 6), indexBuffer.get(start + 5), indexBuffer.get(start + 4));
                index_length = 0xFF & indexBuffer.get(start + 7);
                break;
            }
        }

        byte[] areaBytes;

        lock.lock();
        try {
            dataBuffer.position(offset + (int) index_offset - 1024);
            areaBytes = new byte[index_length];
            dataBuffer.get(areaBytes, 0, index_length);
        } finally {
            lock.unlock();
        }

        return new String(areaBytes, Charset.forName("UTF-8")).split("\t", -1);
    }

    private static void watch() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                long time = ipFile.lastModified();
                if (time > lastModifyTime) {
                    lastModifyTime = time;
                    load();
                }
            }
        }, 1000L, 5000L, TimeUnit.MILLISECONDS);
    }

    private static void load() {
        lastModifyTime = ipFile.lastModified();
        FileInputStream fin = null;
        lock.lock();
        try {
            dataBuffer = ByteBuffer.allocate(Long.valueOf(ipFile.length()).intValue());
            fin = new FileInputStream(ipFile);
            int readBytesLength;
            byte[] chunk = new byte[4096];
            while (fin.available() > 0) {
                readBytesLength = fin.read(chunk);
                dataBuffer.put(chunk, 0, readBytesLength);
            }
            dataBuffer.position(0);
            int indexLength = dataBuffer.getInt();
            byte[] indexBytes = new byte[indexLength];
            dataBuffer.get(indexBytes, 0, indexLength - 4);
            indexBuffer = ByteBuffer.wrap(indexBytes);
            indexBuffer.order(ByteOrder.LITTLE_ENDIAN);
            offset = indexLength;

            int loop = 0;
            while (loop++ < 256) {
                index[loop - 1] = indexBuffer.getInt();
            }
            indexBuffer.order(ByteOrder.BIG_ENDIAN);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
            lock.unlock();
        }
    }

    private static long bytesToLong(byte a, byte b, byte c, byte d) {
        return int2long((((a & 0xff) << 24) | ((b & 0xff) << 16) | ((c & 0xff) << 8) | (d & 0xff)));
    }

    private static int str2Ip(String ip)  {
        String[] ss = ip.split("\\.");
        int a, b, c, d;
        a = Integer.parseInt(ss[0]);
        b = Integer.parseInt(ss[1]);
        c = Integer.parseInt(ss[2]);
        d = Integer.parseInt(ss[3]);
        return (a << 24) | (b << 16) | (c << 8) | d;
    }

    private static long ip2long(String ip)  {
        return int2long(str2Ip(ip));
    }

    private static long int2long(int i) {
        long l = i & 0x7fffffffL;
        if (i < 0) {
            l |= 0x080000000L;
        }
        return l;
    }
}