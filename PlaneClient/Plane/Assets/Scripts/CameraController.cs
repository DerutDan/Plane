using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CameraController : MonoBehaviour {
	public GameObject player1,player2;
	Camera cam;
	// Use this for initialization
	void Start () {
		cam = GetComponent<Camera> ();
	}

	// Update is called once per frame
	void Update () {
		float x1 = player1.transform.transform.position.x;
		float y1 = player1.transform.transform.position.y;
		float x2 = player2.transform.transform.position.x;
		float y2 = player2.transform.transform.position.y;
		transform.position = new Vector3 ((x1+x2)/2, (y1+y2)/2, -10);
		cam.orthographicSize = Mathf.Max (Mathf.Abs(x1 - x2), Mathf.Max (Mathf.Abs(y1 - y2), 10));
	}
}
