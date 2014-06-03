
package com.handee.jsonbeans;

public interface JsonSerializable {
	public void write(Json json);

	public void read(Json json, JsonValue jsonData);
}
