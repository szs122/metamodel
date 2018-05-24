/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.metamodel.hbase;

import java.util.Set;

import org.apache.metamodel.AbstractUpdateCallback;
import org.apache.metamodel.UpdateCallback;
import org.apache.metamodel.create.TableCreationBuilder;
import org.apache.metamodel.delete.RowDeletionBuilder;
import org.apache.metamodel.drop.TableDropBuilder;
import org.apache.metamodel.insert.RowInsertionBuilder;
import org.apache.metamodel.schema.Schema;
import org.apache.metamodel.schema.Table;

/**
 * This class is used to build objects to do client-operations on a HBase datastore
 */
public class HBaseUpdateCallback extends AbstractUpdateCallback implements UpdateCallback {

    private final HBaseClient _hBaseClient;

    public HBaseUpdateCallback(HBaseDataContext dataContext) {
        super(dataContext);
        _hBaseClient = new HBaseClient(dataContext.getConnection());
    }

    @Override
    public TableCreationBuilder createTable(Schema schema, String name) {
        return new HBaseCreateTableBuilder(this, schema, name);
    }

    /**
     * Initiates the building of a table creation operation.
     * @param schema the schema to create the table in
     * @param name the name of the new table
     * @param columnFamilies the columnFamilies of the new table
     * @return {@link HBaseCreateTableBuilder}
     */
    public HBaseCreateTableBuilder createTable(Schema schema, String name, Set<String> columnFamilies) {
        return new HBaseCreateTableBuilder(this, schema, name, columnFamilies);
    }

    @Override
    public boolean isDropTableSupported() {
        return true;
    }

    @Override
    public TableDropBuilder dropTable(Table table) {
        return new HBaseTableDropBuilder(table, this);
    }

    @Override
    public RowInsertionBuilder insertInto(Table table) {
        if (table instanceof HBaseTable) {
            return new HBaseRowInsertionBuilder(this, (HBaseTable) table);
        } else {
            throw new IllegalArgumentException("Not an HBase table: " + table);
        }
    }

    @Override
    public boolean isDeleteSupported() {
        return true;
    }

    @Override
    public RowDeletionBuilder deleteFrom(Table table) {
        if (table instanceof HBaseTable) {
            return new HBaseRowDeletionBuilder(_hBaseClient, (HBaseTable) table);
        } else {
            throw new IllegalArgumentException("Not an HBase table: " + table);
        }
    }

    public HBaseClient getHBaseClient() {
        return _hBaseClient;
    }
}
