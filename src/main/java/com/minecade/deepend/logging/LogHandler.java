package com.minecade.deepend.logging;

/**
 * Created 3/12/2016 for Deepend
 *
 * @author Citymonstret
 */
public interface LogHandler<T, M>
{

    T info(M message);

    T error(M message);

    T debug(M message);
}
