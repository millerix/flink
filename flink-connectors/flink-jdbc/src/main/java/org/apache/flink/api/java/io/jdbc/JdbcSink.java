/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.api.java.io.jdbc;

import org.apache.flink.annotation.PublicEvolving;
import org.apache.flink.api.java.io.jdbc.executor.JdbcBatchStatementExecutor;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.util.function.Function;

/**
 * Facade to create JDBC {@link SinkFunction sinks}.
 */
@PublicEvolving
public class JdbcSink {

	/**
	 * Create a JDBC sink with the default {@link JdbcExecutionOptions}.
	 *
	 * @see #sink(String, JdbcStatementBuilder, JdbcExecutionOptions, JdbcConnectionOptions)
	 */
	public static <T> SinkFunction<T> sink(String sql, JdbcStatementBuilder<T> statementBuilder, JdbcConnectionOptions connectionOptions) {
		return sink(sql, statementBuilder, JdbcExecutionOptions.defaults(), connectionOptions);
	}

	/**
	 * Create a JDBC sink.
	 *
	 * @param sql               arbitrary DML query (e.g. insert, update, upsert)
	 * @param statementBuilder   sets parameters on {@link java.sql.PreparedStatement} according to the query
	 * @param <T>               type of data in {@link org.apache.flink.streaming.runtime.streamrecord.StreamRecord StreamRecord}.
	 * @param executionOptions  parameters of execution, such as batch size and maximum retries
	 * @param connectionOptions parameters of connection, such as JDBC URL
	 */
	public static <T> SinkFunction<T> sink(
			String sql,
			JdbcStatementBuilder<T> statementBuilder,
			JdbcExecutionOptions executionOptions,
			JdbcConnectionOptions connectionOptions) {
		return new GenericJdbcSinkFunction<>(new JdbcBatchingOutputFormat<>(
				new SimpleJdbcConnectionProvider(connectionOptions),
				executionOptions,
				unused -> JdbcBatchStatementExecutor.simple(sql, statementBuilder),
				JdbcBatchingOutputFormat.RecordExtractor.identity()
		));
	}

	private JdbcSink() {
	}
}