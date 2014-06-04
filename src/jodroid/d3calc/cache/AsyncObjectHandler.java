package jodroid.d3calc.cache;

public interface AsyncObjectHandler<T> {
	void onSuccess(T obj);
	void onFailure(String code, String reason);
}
