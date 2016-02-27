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
package com.minecade.deepend.exceptions;

/**
 * Created 2/27/2016 for Deepend
 *
 * @author Citymonstret
 */
public class DeependRuntimeException extends RuntimeException {

    public static final String FORMAT = "<Deepend> %s encountered an exception; %s";

    public DeependRuntimeException(String env, String message) {
        super(String.format(FORMAT, env, message));
    }

    public DeependRuntimeException(String env, String message, Throwable cause) {
        super(String.format(FORMAT, env, message), cause);
    }

}
