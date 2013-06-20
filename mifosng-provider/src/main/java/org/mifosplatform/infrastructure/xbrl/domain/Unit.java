package org.mifosplatform.infrastructure.xbrl.domain;

import java.util.HashMap;
import java.util.Map;

public enum Unit {
	USD(1,"iso4217:USD"),
	AFN(12,"iso4217:AFN"),
	;
	
	private final Integer id;
	private final String code;
	
	private Unit(final Integer id, final String code) {
		this.id = id;
		this.code = code;
	}
	
	private static final Map<Integer, Unit> intToEnumMap = new HashMap<Integer, Unit>();
	static {
		for (final Unit unit : Unit.values()) {
			intToEnumMap.put(unit.id, unit);
		}
	}
	
	public static Unit fromId(final Integer id) {
		return intToEnumMap.get(id);
	}
}
