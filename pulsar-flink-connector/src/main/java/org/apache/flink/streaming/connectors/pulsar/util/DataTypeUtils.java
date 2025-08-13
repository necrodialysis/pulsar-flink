/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.streaming.connectors.pulsar.util;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.flink.table.types.AtomicDataType;
import org.apache.flink.table.types.DataType;
import org.apache.flink.table.types.logical.LogicalType;
import org.apache.flink.table.types.logical.RowType;
import org.apache.flink.table.types.logical.utils.LogicalTypeUtils;
import org.apache.flink.util.Preconditions;
import java.util.Optional;

import org.apache.flink.table.types.FieldsDataType;

import static org.apache.flink.table.types.logical.LogicalTypeRoot.ROW;

/** flink DataType util. */
public class DataTypeUtils {

    @SuppressWarnings("unchecked")
    public static <T> Optional<Class<T>> extractType(DataType dataType) {
        if (dataType instanceof AtomicDataType) {
            final Class<T> conversionClass = (Class<T>) dataType.getConversionClass();
            return Optional.of(conversionClass);
        }
        return Optional.empty();
    }

    /** Removes a string prefix from the fields of the given row data type. */
    public static DataType stripRowPrefix(DataType dataType, String prefix) {
        Preconditions.checkArgument(dataType.getLogicalType().is(ROW), "Row data type expected.");
        final RowType rowType = (RowType) dataType.getLogicalType();
        final List<String> newFieldNames =
                rowType.getFieldNames().stream()
                        .map(
                                s -> {
                                    if (s.startsWith(prefix)) {
                                        return s.substring(prefix.length());
                                    }
                                    return s;
                                })
                        .collect(Collectors.toList());
        final LogicalType newRowType = LogicalTypeUtils.renameRowFields(rowType, newFieldNames);
        return new FieldsDataType(
                newRowType, dataType.getConversionClass(), dataType.getChildren());
    }

}
