package com.lemon95.wyzq.drop;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;

public class WgUdpCommShort { //短报文协议

	public static final int  WGPacketSize = 64;             //报文长度
	public static final byte Type = 0x17; //2015-04-30 08:50:29 0x19;					//类型
	public static final int  ControllerPort = 60000;        //控制器端口
	public static final long SpecialFlag = 0x55AAAA55;      //特殊标识 防止误操作

    public static byte[] longToByte(long number) {   
	     byte[] b = new byte[8];   
	     for (int i = 0; i < 8; i++) {   
		       b[i] = (byte) (number % 256);   
		       number >>= 8;   
		     }   
	     return b;   
	    }
	 
	 //将带符号的bt转换为不带符号的int类型数据 
	 public static int getIntByByte(byte bt)  //bt 转换为无符号的int
	{
	    if (bt <0)
	    {
	    	return (bt+256);
	    }
	    else
	    {
	    	return bt;
	    }
	}
	 
	//从字节转换为 long型数据, 最大长度为8字节 低位在前, 高位在后...
			//bytlen (1--8), 不在此范围则返回 -1
	public static long getLongByByte(byte[] data,int startIndex,int bytlen)
    {
    	long ret =-1;
		if ((bytlen >=1) && (bytlen <=8))
		{
    		ret = getIntByByte(data[startIndex + bytlen-1]);
    		for (int i=1; i<bytlen; i++)
    		{
    			ret <<=8;
    			ret += getIntByByte(data[startIndex + bytlen-1-i]);
    		}
		}
    	return ret;
    }
			
				
	public byte	 functionID;		    //功能号
	public long	 iDevSn;                //设备序列号 4字节
	public byte[]  data= new byte[56];              //56字节的数据 [含流水号]

	private static long _Global_xid = 0;
    protected long _xid = 0; //2011-5-12 15:28:37
    void GetNewXid()  //2011-1-10 14:22:16 获取新的Xid
    {
        _Global_xid++;
        _xid = _Global_xid; //新的值
    }
    static long getXidOfCommand(byte[] cmd) //获取指令中的xid
    {
        long ret = -1;
        if (cmd.length >= WGPacketSize)
        {
            ret = getLongByByte(cmd, 40, 4);
        }
        return ret;
    }

	   public WgUdpCommShort()
		{
			Reset();
		}
		public void Reset()  //数据复位
		{
			for(int i=0; i<data.length; i++)
			{
				data[i] =0;
			}
		}
		public byte[] toByte() //生成64字节指令包
		{
			byte[] buff =new byte[WGPacketSize];
				for(int i=0; i<data.length; i++)
				{
					buff[i] =0;
				}
				buff[0] = Type;
				buff[1] = functionID;
		        System.arraycopy(longToByte(iDevSn), 0, buff, 4, 4);
		        System.arraycopy(data, 0, buff, 8, data.length);

		        GetNewXid();
		        System.arraycopy(longToByte(_xid), 0, buff, 40, 4);
               return buff;
		}
		

	Queue<byte[]> queue;
	IoConnector connector; // = new NioDatagramConnector();
	ConnectFuture connFuture; 
	public  void CommOpen(String ip, int port)
	{
		queue = new LinkedList<byte[]>();
		connector = new NioDatagramConnector();
		connector.setHandler(new WgUdpCommShortHandler(queue));
		connFuture = connector.connect(new InetSocketAddress(ip,	port));
	}
	
	//打开通信连接
	public  void CommOpen(String ip)
	{
		CommOpen(ip,ControllerPort);  //2013-11-06 14:16:52 默认是60000
	}
	
	//关闭通信连接
	public  void CommClose()
	{
		IoSession  session = connFuture.getSession();
	   	if (session !=null)
	   	{
	   		session.close(true);
	   	}
         connector.dispose();
	}
	
	//运行 获取通信数据
	//失败时, 返回 null, 否则为64字节数据
	public byte[] run()
	{
		return getInfo(iDevSn,toByte());
	}
	
	//通过指定sn和command 获取数据
    public  byte[] getInfo( long sn, byte[] command) 
    {
		byte[] bytCommand = command;
        IoBuffer b;
		IoSession session = connFuture.getSession();
		Boolean bSent =false;
		if (session !=null)
		{
			if (session.isConnected())
			{
				b = IoBuffer.allocate(bytCommand.length);
				b.put(bytCommand);
				b.flip();
				session.write(b);
				bSent =true; 
			}
		}
		
         int  bSuccess = 0; 
		 int tries = 3;
		 long xid = getXidOfCommand(bytCommand) ;   
		 byte[] bytget=null;
         while ((tries--) > 0)  
         {
				long startTicks = java.util.Calendar.getInstance().getTimeInMillis(); // DateTime.Now.Ticks;
				long CommTimeoutMsMin = 300;
			    long endTicks = startTicks + CommTimeoutMsMin; 
		       if (startTicks > endTicks)
		       {
		    	   //System.out.println("超时");
		    	   try {
		  				Thread.sleep(30);
		  			} catch (InterruptedException e) {
		  				e.printStackTrace();
		  			}
		    	   continue; 
		       }
		       long startIndex = 0;
		       while (endTicks > java.util.Calendar.getInstance().getTimeInMillis())
		       {
		    	   if (!bSent)  //没有发送过....
		    	   {
		    		   session = connFuture.getSession();
		    		   if (session !=null)
		    			{
		    				if (session.isConnected())
		    				{
		    					b = IoBuffer.allocate(bytCommand.length);
		    					b.put(bytCommand);
		    					b.flip();
		    					session.write(b);
		    					bSent =true; 
		    				}
		    			}
		    	   }
		           if (!queue.isEmpty())
		           {
		        		synchronized(queue)
		        		{
		                		bytget= queue.poll();
		        		}
		                if ((bytget[0]== bytCommand[0]) //类型一致
									&& (bytget[1]== bytCommand[1]) //功能号一致
									&& (xid == getXidOfCommand(bytget)) )  //序列号对应
					    {
		                   bSuccess = 1;
		                   break; // return ret;
		                }
		                else
		                {
		                	//System.out.printf("无效包 xid=%d\r\n", WgUdpComm.getXidOfCommand(bytget));
		                }
		           }
		           else
		           {
		               if ((startTicks + 1) < java.util.Calendar.getInstance().getTimeInMillis()) 
		               {
		               }
		               else if (startIndex > 10)
		               {
		                   try {
		       				Thread.sleep(30);
		       			} catch (InterruptedException e) {
		       				e.printStackTrace();
		       			}
		               }
		               else
		               {
		                   startIndex++;
		                   try {
		       				Thread.sleep(1);
		       				} catch (InterruptedException e) {
		       				e.printStackTrace();
		       				}
		               }
		           }
		
		       }
		       if (bSuccess > 0)
		       {
		    	   break;
		       }
		       else
		       {
		    	  // System.out.println("重试....");
		    	session = connFuture.getSession();
		   		if (session !=null)
		   		{
		   			if (session.isConnected())
		   			{
		   				b = IoBuffer.allocate(bytCommand.length);
				   		b.put(bytCommand);
			            b.flip();
		   				session.write(b);
		   			}
		   		}
		       }
         }
         
         if (bSuccess > 0)
         {
      	   //System.out.println("通信 成功");
       	    return  bytget;
         }
         else
         {
      	  //System.out.println("通信 失败....");
         }
         return null;
 	}

}
