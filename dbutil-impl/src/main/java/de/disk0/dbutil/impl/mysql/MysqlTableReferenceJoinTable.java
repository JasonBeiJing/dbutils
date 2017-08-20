package de.disk0.dbutil.impl.mysql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.disk0.dbutil.api.Comparator;
import de.disk0.dbutil.api.Condition;
import de.disk0.dbutil.api.JoinTable;
import de.disk0.dbutil.api.Operator;
import de.disk0.dbutil.api.TableReference;
import de.disk0.dbutil.impl.util.AliasGenerator;

public class MysqlTableReferenceJoinTable extends MysqlTableReferenceSimple implements JoinTable {

	protected Map<String,Object> params = new HashMap<>();
	
	private TableReference joinTarget;
	private Condition condition;
	
	private boolean left;
	
	protected MysqlTableReferenceJoinTable(AliasGenerator aliasGenerator, TableReference joinTarget, boolean left) {
		super(aliasGenerator);
		this.alias = aliasGenerator.generateAlias("subselect");
		this.left = left;
		this.joinTarget = joinTarget;
	}
	
	@Override
	public String getSql() {
		List<String> cs = new ArrayList<>();
		if(left) {
			cs.add("LEFT");
		}
		cs.add("JOIN");
		cs.add(joinTarget.getSql());
		if(condition!=null) {
			cs.add("ON");
			cs.add(condition.getSql());
		}
		if(refs.size()>0) {
			for(TableReference r : refs) {
				cs.add(r.getSql());
			}
		}
		return StringUtils.join(cs.toArray()," ");
	}

	@Override
	public Map<String, Object> getParams() {
		Map<String, Object> out = new HashMap<>();
		out.putAll(params);
		if(joinTarget!=null) {
			out.putAll(joinTarget.getParams());
		}
		if(condition!=null) {
			out.putAll(condition.getParams());
		}
		for(TableReference tr : refs) {
			out.putAll(tr.getParams());
		}
		return out;
	}

	@Override
	public String getAlias() {
		return joinTarget.getAlias();
	}

	@Override
	public String getName() {
		return joinTarget.getName();
	}
	
	@Override
	public Condition addOn(Operator op, TableReference table1, String field1, Comparator c, TableReference table2, String field2) {
		if(condition==null) {
			condition = new MysqlCondition(this.aliasGenerator);
		}
		return condition.condition(op, table1, field1, c, table2, field2);
	}

	@Override
	public Condition addOn(Operator op, TableReference table1, String field1, Comparator c, Object value) {
		if(condition==null) {
			condition = new MysqlCondition(this.aliasGenerator);
		}
		return condition.condition(op, table1, field1, c, value);
	}
}