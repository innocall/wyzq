package com.lemon95.wyzq.utils;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * 软引用的map集合
 * 
 * @author Administrator
 * 
 * @param <K>
 * @param <V>
 */
public class SoftMap<K, V> extends HashMap<K, V> {
	// 降低对象的引用界别�?—V

	private HashMap<K, SoftValue<K, V>> temp;// 存放袋子的集�?
	private ReferenceQueue<V> queue;

	public SoftMap() {
		// Object v=new Object();//占用内存较多
		// SoftReference sr=new SoftReference(v);//v的引用级别被降低�?

		// 第一步：将占用内存较多的手机，添加到袋子�?
		// 第二步：手机被GC回收，清理空袋子

		temp = new HashMap<K, SoftValue<K, V>>();
		queue = new ReferenceQueue<V>();// 但凡装V的袋子，都可以放到该集合�?
	}

	@Override
	public V put(K key, V value) {
		// SoftReference<V> sr = new SoftReference<V>(value);// 将手机装到袋子中
		// SoftReference(T referent, ReferenceQueue<? super T> q)

		// 记录回收掉的袋子引用ReferenceQueue
		SoftValue<K, V> sr = new SoftValue<K, V>(key, value, queue);
		temp.put(key, sr);
		return null;
	}

	@Override
	public V get(Object key) {
		clearsr();
		SoftValue<K, V> sr = temp.get(key);
		if (sr != null) {
			// 垃圾回收器清除，则此方法将返�?null�?
			return sr.get();
		}

		return null;
	}

	@Override
	public boolean containsKey(Object key) {
		// �?��叫真正的contain
		// temp.containsKey(key);
		/*
		 * if(get(key)!=null) { return true; }else{ return false; }
		 */
		return get(key) != null;
	}

	/**
	 * 清理空袋�?
	 */
	private void clearsr() {
		// 方式�?��循环temp，�?�?��断是否有手机，存在问题：如果当前内存够用，循环无�?
		// 方式二：GC知道把那个袋子的手机给回收了，利用GC，让其记录曾经被偷的手机（存入到集合中）
		// 如果能够控制该集合，再循环该集合，里面存放的�?��被回收了
		// 轮询此队列，查看是否存在可用的引用对象�?
		// 如果存在�?��立即可用的对象，则从该队列中"移除"此对象并返回�?
		// 否则此方法立即返�?null�?
		SoftValue<K, V> poll = (SoftValue<K, V>) queue.poll();
		while (poll != null) {
			temp.remove(poll.key);
			poll = (SoftValue<K, V>) queue.poll();
		}
	}

	/**
	 * 增强版的袋子，增加了key，方便清理操�?
	 * 
	 * @author Administrator
	 * 
	 * @param <K>
	 * @param <V>
	 */
	private class SoftValue<K, V> extends SoftReference<V> {
		private Object key;

		public SoftValue(K key, V r, ReferenceQueue<? super V> q) {
			super(r, q);
			this.key = key;
		}

	}
}
