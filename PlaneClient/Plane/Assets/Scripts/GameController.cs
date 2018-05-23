
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Net.Sockets;
using System.Net;
using System;
using System.IO;
using UnityEngine.UI;

public class GameController : MonoBehaviour {
	public GameObject p1, p2, bullet;
	Camera mainCamera;
	string haddress = "";
	int port = 4444;
	//volatile float x1 = 0, y1 = 0, angle1 = 0,x2 = 0,y2 = 0,angle2 = 0;
	Socket client;
	byte player = 0, playerCount = 0;
	volatile List<GameObject> bullets1 = new List<GameObject>(),
							bullets2 = new List<GameObject>();

	private bool connected = false;
	private MDecoder md;


	// Use this for initialization
	void Start () {
		p1.SetActive(false);
		p2.SetActive(false);
	}
	// Update is called once per frame
	void Update () {
		if (connected)
		{
			byte[] bsize = new byte[4];
			client.Receive(bsize);
			int size = BitConverter.ToInt32(bsize, 0);
			byte[] data = new byte[size];
			int k = client.Receive(data);
			if (k != size)
			{
				print("Alarm!");
			}

			//MemoryStream ms = new MemoryStream (data);
			md.aquireBuf(data);
			byte[] bint = new byte[4];
			byte[] bdouble = new byte[8];
			if ( /*ms.ReadByte()*/md.readByte() == 1)
			{
				//First Plane parsing
				/*ms.Read (bdouble, 0, 8);
				float x = (float)BitConverter.ToDouble (bdouble, 0);
				ms.Read (bdouble, 0, 8);
				float y = (float)BitConverter.ToDouble (bdouble, 0);
				ms.Read (bdouble, 0, 8);
				float angle = (float)BitConverter.ToDouble (bdouble, 0);*/
				float x = md.readFloat();
				float y = md.readFloat();
				float angle = md.readFloat();
				p1.transform.position = new Vector3(x, y, 0);
				p1.transform.eulerAngles = new Vector3(0, 0, angle);
				print(x + " " + y + " " + angle);
				//First Plane Bullets parsing
				//ms.Read (bint, 0, 4);
				int bulletSize1 = md.readInt(); //BitConverter.ToInt32 (bint, 0);
				for (int i = 0; i < bulletSize1; ++i)
				{
					/*ms.Read (bdouble, 0, 8);
					x = (float)BitConverter.ToDouble (bdouble, 0);
					ms.Read (bdouble, 0, 8);
					y = (float)BitConverter.ToDouble (bdouble, 0);*/
					//ms.Read (bdouble, 0, 8);
					//angle = (float)BitConverter.ToDouble (bdouble, 0);
					x = md.readFloat();
					y = md.readFloat();
					if (i >= bullets1.Count)
					{
						bullets1.Add(Instantiate(bullet));
					}

					bullets1[i].transform.position = new Vector3(x, y, 0);
					bullets1[i].SetActive(true);
					//bullets1[i].transform.eulerAngles = new Vector3 (0, 0, angle);
				}

				for (int i = bulletSize1; i < bullets1.Count; ++i)
				{
					bullets1[i].SetActive(false);
				}

				//Second Plane parsing
				/*ms.Read (bdouble, 0, 8);
				x = (float)BitConverter.ToDouble (bdouble, 0);
				ms.Read (bdouble, 0, 8);
				y = (float)BitConverter.ToDouble (bdouble, 0);
				ms.Read (bdouble, 0, 8);
				angle = (float)BitConverter.ToDouble (bdouble, 0);*/
				x = md.readFloat();
				y = md.readFloat();
				angle = md.readFloat();
				p2.transform.position = new Vector3(x, y, 0);
				p2.transform.eulerAngles = new Vector3(0, 0, angle);

				//Second Plane Bullets Parsing
				//ms.Read (bint, 0, 4);
				int bulletSize2 = md.readInt(); //BitConverter.ToInt32 (bint, 0);
				for (int i = 0; i < bulletSize2; ++i)
				{
					/*ms.Read (bdouble, 0, 8);
					x = (float)BitConverter.ToDouble (bdouble, 0);
					ms.Read (bdouble, 0, 8);
					y = (float)BitConverter.ToDouble (bdouble, 0);*/
					//ms.Read (bdouble, 0, 8);
					//angle = (float)BitConverter.ToDouble (bdouble, 0);
					x = md.readFloat();
					y = md.readFloat();
					if (i >= bullets2.Count)
					{
						bullets2.Add(Instantiate(bullet));
					}

					bullets2[i].transform.position = new Vector3(x, y, 0);
					bullets2[i].SetActive(true);
					//bullets2[i].transform.eulerAngles = new Vector3 (0, 0, angle);
				}

				for (int i = bulletSize2; i < bullets2.Count; ++i)
				{
					bullets2[i].SetActive(false);
				}
			}
			else if (data[0] == 0)
			{
				player = data[1];
				playerCount = data[2];
			}

			if (player == 1)
			{
				if (Input.GetKeyDown(KeyCode.UpArrow))
				{
					byte[] b = {2, 0, 0};
					client.Send(b);
				}
				else if (Input.GetKeyUp(KeyCode.UpArrow))
				{
					byte[] b = {2, 0, 1};
					client.Send(b);
				}
				else if (Input.GetKeyDown(KeyCode.DownArrow))
				{
					byte[] b = {2, 0, 2};
					client.Send(b);
				}
				else if (Input.GetKeyUp(KeyCode.DownArrow))
				{
					byte[] b = {2, 0, 3};
					client.Send(b);
				}

				if (Input.GetKeyDown(KeyCode.Space))
				{
					byte[] b = {2, 0, 4};
					client.Send(b);
				}
			}

			if (player == 2)
			{
				if (Input.GetKeyDown(KeyCode.UpArrow))
				{
					byte[] b = {2, 0, 2};
					client.Send(b);
				}
				else if (Input.GetKeyUp(KeyCode.UpArrow))
				{
					byte[] b = {2, 0, 3};
					client.Send(b);
				}
				else if (Input.GetKeyDown(KeyCode.DownArrow))
				{
					byte[] b = {2, 0, 0};
					client.Send(b);
				}
				else if (Input.GetKeyUp(KeyCode.DownArrow))
				{
					byte[] b = {2, 0, 1};
					client.Send(b);
				}

				if (Input.GetKeyDown(KeyCode.Space))
				{
					byte[] b = {2, 0, 4};
					client.Send(b);
				}
			}
		}
	}
	void OnApplicationQuit() {
		client.Close ();
	}
	public void findGame(GameObject canvas) {
		canvas.SetActive(false);
		IPHostEntry ipHost = Dns.GetHostEntry(haddress);
		IPAddress ipAddr = ipHost.AddressList[0];
		IPEndPoint ipEndPoint = new IPEndPoint(ipAddr, port);
		client = new Socket (ipAddr.AddressFamily, SocketType.Stream, ProtocolType.Tcp);
		print ("Starting connection");
		client.Connect (ipEndPoint);
		print ("Connected");
		md = new MDecoder();
		p1.SetActive(true);
		p2.SetActive(true);
		connected = true;
	}

	public void changeIP(Text text)
	{
		haddress = text.text;
	}
}

class MDecoder {
	private MemoryStream stream;
	private byte[] intBuf, doubleBuf;

	public MDecoder()
	{
		intBuf = new byte[4];
		doubleBuf = new byte[8];
	}

	public void aquireBuf(byte[] b)
	{
		stream = new MemoryStream(b);
	}
	double readDouble()
	{
		stream.Read (doubleBuf, 0, 8);
		return BitConverter.ToDouble (doubleBuf, 0);
	}

	public float readFloat()
	{
		stream.Read (intBuf, 0, 4);
		return BitConverter.ToSingle (intBuf, 0);
	}

	public int readInt()
	{
		stream.Read (intBuf, 0, 4);
		return BitConverter.ToInt32 (intBuf, 0);
	}

	public int readByte()
	{
		return stream.ReadByte();
	}
}