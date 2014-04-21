package edu.njust.sem.wa.test;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.JSONWriter;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.NameFilter;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.ValueFilter;

public class JsonTest {
	public static void main(String[] args) throws IOException, Exception {
		/*
		 * 1.将指定的 JavaBean对象解析成Json字符串
		 */
		Person p1 = new Person("zhangsan", 22, 1332117);
		System.out.println(JSON.toJSONString(p1)); // 字段的get属性

		/*
		 * 2.将封装了javaBean对象的集合解析成json字符串,并对该数据进行过滤
		 */
		ValueFilter valueFilter = new ValueFilter() {
			@Override
			public Object process(Object source, String name, Object value) {
				if (value.equals("lisi")) {
					return "**敏感词汇**";
				}
				return value;
			}
		};
		List<Person> list = new ArrayList<Person>();
		list.add(new Person("lisi", 19, 159785));
		list.add(new Person("wangwu", 10, 113123213));
		list.add(new Person("zhaoliu", 29, 1444785));
		System.out.println(JSON.toJSONString(list, valueFilter));

		/*
		 * 3.将List<Map<String, Person>>集合 转换为json字符串
		 */
		List<Map<String, Person>> list2 = new ArrayList<Map<String, Person>>();

		Map<String, Person> map1 = new HashMap<String, Person>();
		map1.put("001", new Person("wangwu", 19, 159785));
		map1.put("002", new Person("lisi", 19, 159785));
		map1.put("003", new Person("lisi", 19, 159785));
		Map<String, Person> map2 = new HashMap<String, Person>();
		map2.put("001", new Person("zhaoliu", 19, 159785));
		map2.put("002", new Person("lisi", 19, 159785));
		map2.put("003", new Person("lisi", 19, 159785));
		list2.add(map1);
		list2.add(map2);
		System.out.println(JSON.toJSONString(list2));

		/*
		 * 4.将指定的字符串解析成 指定的javaBean对象(使用泛型)
		 */
		Person p2 = JSON.parseObject(Person.getUtil(1), Person.class); // 字段的set属性

		/*
		 * 5.将指定的字符串解析成 封装了指定javaBean对象的集合
		 */
		// 方法一：
		List<Person> list1 = JSON.parseArray(Person.getUtil(2), Person.class);
		// 方法二：
		List<Person> list4 = JSON.parseObject(Person.getUtil(2),
				new TypeReference<List<Person>>() {
				});

		/*
		 * 6.将json字符串 解析成 List<Map<String, Person>>集合 ，注意Type类型的定义
		 */
		// 关键type类型
		List<Map<String, Person>> list3 = JSON.parseObject(Person.getUtil(3),
				new TypeReference<List<Map<String, Person>>>() {
				});

		/*
		 * 7.将json字符串解析为 JsonObject对象， 由于该对象继承了map，可以得到键值对
		 */
		JSONObject object = (JSONObject) JSON.parse(Person.getUtil(1));
		System.out.println("name:" + object.getString("name"));
		System.out.println("age:" + object.getIntValue("age"));
		System.out.println("number:" + object.getIntValue("number"));

		/*
		 * 8.name过滤器，只能对name进行更新,不管是否显示
		 */
		NameFilter filter = new NameFilter() {
			@Override
			public String process(Object source, String name, Object value) {
				// source是当前对象， name是key， value实在值
				if ("age".equals(name)) {
					return "AGE";
				}
				return name;
				// {"age":22,"name":"zhangsan","number":1332117} //过滤前
				// {"AGE":22,"name":"zhangsan","number":1332117} //过滤后
			}
		};

		JSONSerializer jsonSerializer = new JSONSerializer();
		jsonSerializer.getNameFilters().add(filter); // 通过增加一个过滤器，为name和值进行过滤
		jsonSerializer.write(p1);
		System.out.println(jsonSerializer.toString());

		/*
		 * 9.属性过滤器PropertyFilter，满足要求的可以不做显示
		 */
		PropertyFilter propertyFilter = new PropertyFilter() {
			@Override
			public boolean apply(Object source, String name, Object value) {
				if ("age".equals(name)) {
					return true;
				}
				return false;
			}
		};

		JSONSerializer jsonSerializer2 = new JSONSerializer();
		jsonSerializer2.getPropertyFilters().add(propertyFilter);
		jsonSerializer2.write(list);
		System.out.println(jsonSerializer2.toString());

		/*
		 * 10.值过滤器ValueFilter，对满足要求的可以不做显示
		 */
		ValueFilter valueFilter2 = new ValueFilter() {
			@Override
			public Object process(Object source, String name, Object value) {
				if (value.equals(10)) {
					return null;
				}
				return value;
			}
		};

		JSONSerializer jsonSerializer3 = new JSONSerializer();
		jsonSerializer3.getValueFilters().add(valueFilter2);
		jsonSerializer3.write(list);
		System.out.println(jsonSerializer3.toString());

		/*
		 * 11.在本地路径下读取文件的json字符串信息，得到数据并赋值javaBean对象
		 */
		JSONReader reader = new JSONReader(new FileReader("1.txt"));
		// 注意type的使用
		List<Person> list10 = reader
				.readObject(new TypeReference<List<Person>>() {
				}.getType());
		reader.close();

		/*
		 * 12.将Object对象保存至本地路径中，保存为json字符串
		 */
		JSONWriter writer = new JSONWriter(new FileWriter("2.txt"));
		writer.writeObject(list10);
		writer.close();

		/*
		 * 13.在网络上获取json数据，并保存为对应的javaBean对象信息
		 */
		List<Person> list11 = null;
		URL url = new URL("http://192.168.117.114:8080/Test/My");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(3000);
		if (conn.getResponseCode() == 200) {
			InputStream in = conn.getInputStream();
			// 通过JsonReader类得到发出的输出流对象
			JSONReader reader2 = new JSONReader(new InputStreamReader(in));
			// 得到Object对象
			list11 = reader2.readObject(new TypeReference<List<Person>>() {
			}.getType());
		}
		for (Person p : list11) {
			System.out.println(p);
		}

		/*
		 * 14.将客户端的javaBean对象，上传至服务器
		 */
		byte[] bytes = JSON.toJSONBytes(list11);
		URL url2 = new URL("http://192.168.117.114:8080/Test/My");
		HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();
		conn2.setRequestMethod("POST");
		conn2.setConnectTimeout(3000);
		conn2.setRequestProperty("content-length", String.valueOf(bytes.length));
		conn2.setRequestProperty("content-type",
				"application/x-www-form-urlencoded");
		conn2.setDoOutput(true);
		OutputStream os = conn2.getOutputStream();
		os.write(bytes, 0, bytes.length);
		os.close();
		if (conn2.getResponseCode() == 200) {
			System.out.println("上传成功！ ");
		}
	}

	// ////////////////////22222222
	// SerialFeature 字段详细用法
}

class Person {
	public static String getUtil(int type) {
		switch (type) {
		case 1:
			return new String(
					"{\"ag\te\":22,\"name\":\"zhangsan\",\"number\":1332117}");
		case 2:
			return new String(
					"[{\"age\":19,\"name\":\"lisi\",\"number\":159785},"
							+ "{\"age\":10,\"name\":\"wangwu\",\"number\":113123213},"
							+ "{\"age\":29,\"name\":\"zhaoliu\",\"number\":1444785}]");
		case 3:
			return new String(
					"[{\"001\":{\"age\":19,\"name\":\"lisi\",\"number\":159785},"
							+ "\"002\":{\"age\":19,\"name\":\"wangwu\",\"number\":159785},"
							+ "\"003\":{\"age\":19,\"name\":\"lisi\",\"number\":159785}},"
							+ "{\"001\":{\"age\":23,\"name\":\"lisi\",\"number\":159785},"
							+ "\"002\":{\"age\":19,\"name\":\"lisi\",\"number\":159785},"
							+ "\"003\":{\"age\":19,\"name\":\"lisi\",\"number\":159785}}]");
		}
		return null;
	}

	private String name;
	private int age;
	private int number;

	public Person() {
		super();
	}

	public Person(String name, int age, int number) {
		super();
		this.name = name;
		this.age = age;
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	@Override
	public String toString() {
		return "Person [age=" + age + ", name=" + name + ", number=" + number
				+ "]";
	}
}