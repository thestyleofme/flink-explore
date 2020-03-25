package org.abigballofmud.flink.client.request;

import org.abigballofmud.flink.common.Resource;

/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/25 17:07
 * @since 1.0
 */
public interface SubmitRequest extends Request {

    Resource getResource();

    boolean isYarn();
}
