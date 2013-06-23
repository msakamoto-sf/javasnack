/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2004, DbUnit.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.dbunit.dataset.csv;

import java.io.File;

import org.dbunit.dataset.CachedDataSet;
import org.dbunit.dataset.DataSetException;

/**
 * This class constructs an IDataSet given a directory containing CSV files. It handles translations of "null"(the
 * string), into null.
 * 
 * @author Lenny Marks (lenny@aps.org)
 * @author Last changed by: $Author: gommma $
 * @version $Revision: 770 $ $Date: 2008-08-05 04:30:27 +0900 (火, 05 8 2008) $
 * @since Sep 12, 2004 (pre 2.3)
 */
public class CsvBase64BinarySafeDataSet extends CachedDataSet {
    public static final String TABLE_ORDERING_FILE = "table-ordering.txt";

    public CsvBase64BinarySafeDataSet(File dir) throws DataSetException {
        super(new CsvBase64BinarySafeProducer(dir));
    }
}
