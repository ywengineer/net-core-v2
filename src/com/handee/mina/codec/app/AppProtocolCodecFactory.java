/*******************************************************************
 * 
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 * 
 * 成都撼地科技有限责任公司 版权所有
 * 
 * AppProtocalCodecFactory.java
 * 
 * 2013 2013-6-8 上午10:31:49
 * 
 *******************************************************************/
package com.handee.mina.codec.app;

import com.handee.net.message.AppMessage;
import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;

/**
 * 
 * class description
 * 
 * @author Mark
 * 
 */
public class AppProtocolCodecFactory extends DemuxingProtocolCodecFactory {
    public AppProtocolCodecFactory() {
        super.addMessageDecoder(AppMessageDecoder.class);
        super.addMessageEncoder(AppMessage.class, AppMessageEncoder.class);
    }
}
