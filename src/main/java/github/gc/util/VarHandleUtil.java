package github.gc.util;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class VarHandleUtil {

	private static final Map<Key, VarHandle> CACHE = new ConcurrentHashMap<>();

	public static VarHandle getVarHandle(Class<?> clazz, String fieldName, Class<?> fieldType) {
		Key key = new Key(clazz, fieldName, fieldType);
		return CACHE.computeIfAbsent(key, VarHandleUtil::doGetVarHandle);
	}

	private static VarHandle doGetVarHandle(Key key) {
		Class<?> clazz = key.getClazz();
		String fieldName = key.getFieldName();
		Class<?> fieldType = key.getFieldType();
		try {
			return MethodHandles.privateLookupIn(clazz, MethodHandles.lookup())
					.findVarHandle(clazz, fieldName, fieldType);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	// 用于缓存的键，包含类、字段名和字段类型
	private static final class Key {
		private final Class<?> clazz;
		private final String fieldName;
		private final Class<?> fieldType;

		Key(Class<?> clazz, String fieldName, Class<?> fieldType) {
			this.clazz = clazz;
			this.fieldName = fieldName;
			this.fieldType = fieldType;
		}

		public Class<?> getClazz() {
			return clazz;
		}

		public String getFieldName() {
			return fieldName;
		}

		public Class<?> getFieldType() {
			return fieldType;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Key key)) {
				return false;
			}

			return Objects.equals(clazz, key.clazz) && Objects.equals(fieldName, key.fieldName) && Objects.equals(
					fieldType, key.fieldType);
		}

		@Override
		public int hashCode() {
			return Objects.hash(clazz, fieldName, fieldType);
		}
	}
}
