package com.nowto.springboot.starter.codeenum;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 由于Mybatis的EnumTypeHandler、EnumOrdinalTypeHandler不符合业务上枚举类型映射的要求，故创建该类完成枚举类型的数据库映射
 * @author liweibo
 */
@MappedJdbcTypes({JdbcType.TINYINT, JdbcType.SMALLINT, JdbcType.BIGINT, JdbcType.INTEGER})
@MappedTypes(BaseCodeEnum.class)
public class CodeEnumTypeHandler<E extends Enum<E> & BaseCodeEnum> extends BaseTypeHandler<E> {
    private final Class<E> type;

    public CodeEnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int i = rs.getInt(columnName);
        if (i == 0 && rs.wasNull()) {
            return null;
        } else {
            try {
                return CodeEnumUtil.codeOf(type, i);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Cannot convert " + i + " to " + type.getSimpleName() + " by code value.", ex);
            }
        }
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int i = rs.getInt(columnIndex);
        if (i == 0 && rs.wasNull()) {
            return null;
        } else {
            try {
                return CodeEnumUtil.codeOf(type, i);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Cannot convert " + i + " to " + type.getSimpleName() + " by code value.", ex);
            }
        }
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int i = cs.getInt(columnIndex);
        if (i == 0 && cs.wasNull()) {
            return null;
        } else {
            try {
                return CodeEnumUtil.codeOf(type, i);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Cannot convert " + i + " to " + type.getSimpleName() + " by code value.", ex);
            }
        }
    }
}