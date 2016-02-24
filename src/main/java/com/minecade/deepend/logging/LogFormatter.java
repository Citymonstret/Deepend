/*
 * Copyright 2016 Minecade
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.minecade.deepend.logging;

import com.minecade.deepend.object.ObjectGetter;
import lombok.RequiredArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * A simple log formatter
 */
@RequiredArgsConstructor
public class LogFormatter extends Formatter {

    private static final String format = "[%s][%s][%s] %s> %s\n";

    final ObjectGetter<String, String> bundle;
    final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.S");

    @Override
    public String format(LogRecord record) {
        String message = record.getMessage();

        if (bundle != null && bundle.containsKey(message)) {
            message = bundle.get(message);
        }

        String type;

        if (record.getLevel() == Level.INFO) {
            type = "INFO";
        } else if (record.getLevel() == Level.WARNING) {
            type = "DEBUG";
        } else if (record.getLevel() == Level.SEVERE) {
            type = "ERROR";
        } else {
            type = "??";
        }

        return String.format(format, dateFormat.format(new Date()),Thread.currentThread().getName(), record.getLoggerName(), type, message);
    }
}
