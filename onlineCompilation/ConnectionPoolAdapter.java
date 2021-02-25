package cn.com.dhcc.idata.system.datasource.jdbc.interfaceAdapter.impl;

import java.sql.Connection;

import org.apache.commons.dbcp.BasicDataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zaxxer.hikari.HikariDataSource;

import cn.com.dhcc.idata.common.constant.DataTypeConstant;
import cn.com.dhcc.idata.common.entity.datasource.self.RelationalDatabase;
import cn.com.dhcc.idata.common.entity.datasource.self.relationalConfig.ConnectionPoolProperty;
import cn.com.dhcc.idata.system.datasource.jdbc.interfaceAdapter.ConnectionPool;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Data connection pool implementation class. <br>
 * If subclasses need to use connection pool, they can choose by themselves
 * according to the performance comparison of database connection pool. They
 * only need to inherit the instance object method
 * 
 * @author GDP
 * @date 2021年2月23日
 */
@Slf4j
public class ConnectionPoolAdapter implements ConnectionPool {

	/**
	 * <P>
	 * This method creates different connection pool objects.<br>
	 * according to the <code>switch<code> function and to create connection pool
	 * type parameters
	 * 
	 * @param database A user-defined connection entity class, information of
	 *                 connecting database.
	 * @return Connection Return a connection object to operate the database
	 */
	public static Connection getConnection(RelationalDatabase database) {
		Connection connection = null;
		ConnectionPoolAdapter connectionPoolAdapter = new ConnectionPoolAdapter();
		int connectionPoolType = Integer.parseInt(database.getConnectionPoolType());
		switch (connectionPoolType) {
		case DataTypeConstant.SOURCE_TYPE_RELATIONAL_DRUIDPOOL:
			connection = connectionPoolAdapter.druidPool(database);
			break;
		case DataTypeConstant.SOURCE_TYPE_RELATIONAL_DBCPPOOL:
			connection = connectionPoolAdapter.dbcpPoll(database);
			break;
		case DataTypeConstant.SOURCE_TYPE_RELATIONAL_C3P0POOL:
			connection = connectionPoolAdapter.c3p0Pool(database);
			break;
		case DataTypeConstant.SOURCE_TYPE_RELATIONAL_HIKARICPPOOL:
			connection = connectionPoolAdapter.hikariCPPool(database);
			break;
		default:
			break;
		}
		return connection;
	}

	@Override
	public Connection druidPool(RelationalDatabase database) {
		ConnectionPoolProperty property = database.getConnectionPoolProperty();
		try (DruidDataSource druidDataSource = new DruidDataSource()) {
			druidDataSource.setDriverClassName(database.getDriverName());
			druidDataSource.setUrl(database.getUrl());
			druidDataSource.setUsername(database.getUsername());
			druidDataSource.setPassword(database.getPassword());
			// When the parameter value is greater than 0, set the parameter otherwise use
			// the default value
			if (property != null) {
				if (property.getInitialSize() > 0)
					druidDataSource.setInitialSize(property.getInitialSize());
				if (property.getMaxActive() > 0)
					druidDataSource.setMaxActive(property.getMaxActive());
				if (property.getMinIdle() > 0)
					druidDataSource.setMinIdle(property.getMinIdle());
				if (property.getMaxWait() > 0)
					druidDataSource.setMaxWait(property.getMaxWait());
				if (property.getRemoveAbandonedTimeout() > 0)
					druidDataSource.setRemoveAbandonedTimeout(property.getRemoveAbandonedTimeout());
			}
			DruidPooledConnection connection = null;
			try {
				connection = druidDataSource.getConnection();
			} catch (Exception e) {
				log.error("druid connection pool creation exception->{}", e);
			}
			return connection;
		}
	}

	@Override
	public Connection hikariCPPool(RelationalDatabase database) {
		ConnectionPoolProperty property = database.getConnectionPoolProperty();
		try (HikariDataSource hikariDataSource = new HikariDataSource()) {
			hikariDataSource.setDriverClassName(database.getDriverName());
			hikariDataSource.setJdbcUrl(database.getUrl());
			hikariDataSource.setUsername(database.getUsername());
			hikariDataSource.setPassword(database.getPassword());
			// When the parameter value is greater than 0, set the parameter otherwise use
			// the default value
			if (property != null) {
				if (property.getMaxActive() > 0)
					hikariDataSource.setMaximumPoolSize(property.getMaxActive());
				if (property.getMinIdle() > 0)
					hikariDataSource.setMinimumIdle(property.getMinIdle());
				if (property.getMaxWait() > 0)
					hikariDataSource.setConnectionTimeout(property.getMaxWait());
				if (property.getRemoveAbandonedTimeout() > 0)
					hikariDataSource.setIdleTimeout(property.getRemoveAbandonedTimeout());
				// hikariDataSource.set
			}
			Connection connection = null;
			try {
				connection = hikariDataSource.getConnection();
			} catch (Exception e) {
				log.error("hikariCP connection pool creation exception->{}", e);
			}
			return connection;
		}
	}

	@Override
	public Connection c3p0Pool(RelationalDatabase database) {
		ConnectionPoolProperty property = database.getConnectionPoolProperty();
		Connection connection = null;
		ComboPooledDataSource c3p0DataSource = new ComboPooledDataSource();
		try {
			c3p0DataSource.setDriverClass(database.getDriverName());
			c3p0DataSource.setJdbcUrl(database.getUrl());
			c3p0DataSource.setUser(database.getUsername());
			c3p0DataSource.setPassword(database.getPassword());
			// When the parameter value is greater than 0, set the parameter otherwise use
			// the default value
			if (property != null) {
				if (property.getInitialSize() > 0)
					c3p0DataSource.setInitialPoolSize(property.getInitialSize());
				if (property.getMaxActive() > 0)
					c3p0DataSource.setMaxPoolSize(property.getMaxActive());
				if (property.getMinIdle() > 0)
					c3p0DataSource.setMinPoolSize(property.getMinIdle());
				if (property.getRemoveAbandonedTimeout() > 0)
					c3p0DataSource.setMaxIdleTime(property.getRemoveAbandonedTimeout());
				if (property.getMaxWait() > 0)
					c3p0DataSource.setCheckoutTimeout((int) property.getMaxWait());
			}
			connection = c3p0DataSource.getConnection();
		} catch (Exception e) {
			log.error("c3p0 connection pool creation exception->{}", e);
		}
		return connection;
	}

	@Override
	public Connection dbcpPoll(RelationalDatabase database) {
		ConnectionPoolProperty property = database.getConnectionPoolProperty();
		BasicDataSource basicDataSource = new BasicDataSource();
		basicDataSource.setDriverClassName(database.getDriverName());
		basicDataSource.setUrl(database.getUrl());
		basicDataSource.setUsername(database.getUsername());
		basicDataSource.setPassword(database.getPassword());
		// When the parameter value is greater than 0, set the parameter otherwise use
		// the default value
		if (property != null) {
			if (property.getInitialSize() > 0)
				basicDataSource.setInitialSize(property.getInitialSize());
			if (property.getMinIdle() > 0)
				basicDataSource.setMinIdle(property.getMinIdle());
			if (property.getMaxActive() > 0)
				basicDataSource.setMaxActive(property.getMaxActive());
			if (property.getMaxWait() > 0)
				basicDataSource.setMaxWait(property.getMaxWait());
			if (property.getRemoveAbandonedTimeout() > 0)
				basicDataSource.setRemoveAbandonedTimeout(property.getRemoveAbandonedTimeout());
		}
		Connection connection = null;
		try {
			connection = basicDataSource.getConnection();
		} catch (Exception e) {
			log.error("dbcp connection pool creation exception->{}", e);
		}
		return connection;
	}

}
