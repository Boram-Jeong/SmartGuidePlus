package com.example.smartguideplus.clientmodel;

import java.util.ArrayList;

public class GuideFile {
	
	ArrayList<Scene> scene;
	
	public GuideFile() {
		scene = new ArrayList<Scene>();
		
	}

	
	public void addScene(int index) {
		this.scene.add(index, new Scene(index));
	}
	
	public void addScene(int index, Scene scene) {
		this.scene.add(index, scene);
	}
	
	// Scene�� �����Ѵ�. �ϳ��� Scene�� �ִ� ���� 1���� ĸ���̹���, ��������, ��Ʈ������ ���� �� �� �ִ�.
	// 0 : CaptureImage
	// 1 : Voice Record
	// 2 : Note
	public void deleteScene(int index) {
		
		this.scene.remove(index);
	}
	
	
	public boolean isEmpty() {
		return this.scene.isEmpty();
	}
	
	public Scene getScene(int index) {
		return this.scene.get(index);
	}
	
	public int getSceneSize() {
		return this.scene.size();
	}
	
	@Override
	public String toString() {
		return "DataInfo [type=";
	}
	
	// ���̵� ������ �����Ѵ�.
	// ĸ�� �̹��� / ���� �� ���� �������
	public void SaveGuideFile() {
		
	}


	
}
