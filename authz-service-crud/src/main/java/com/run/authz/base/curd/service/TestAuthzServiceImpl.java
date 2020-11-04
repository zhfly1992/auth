/*
 * File name: TestAuthzServiceImpl.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhabing 2018年6月1日 ... ...
 * ...
 *
 **************************************************

package com.run.authz.base.curd.service;

import java.io.IOException;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.run.authz.api.base.crud.TestAuthzService;
import com.run.authz.api.constants.AuthzConstants;
import com.run.entity.common.RpcResponse;
import com.run.reliableNews.server.NewsService;
import com.run.usc.base.util.MongoTemplateUtil;

*//**
 * @Description:
 * @author: zhabing
 * @version: 1.0, 2018年6月1日
 *//*
@Component
public class TestAuthzServiceImpl implements TestAuthzService, ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private Connection			connection;

	@Autowired
	private MongoTemplateUtil	tenementTemplateUtil;
	@Autowired
	private NewsService			newService;

	private Logger				logger	= Logger.getLogger(AuthzConstants.LOGKEY);



	*//**
	 * @see com.run.authz.api.base.crud.TestAuthzService#testSave()
	 *//*
	@Override
	public boolean testSave(String userId) {
		boolean isSucc = false;
		JSONObject testS = new JSONObject();
		testS.put("_id", UUID.randomUUID().toString());
		testS.put("userId", userId);
		testS.put("message", "这是权限中心一条废弃测试数据!!");
		RpcResponse<JSONObject> result = tenementTemplateUtil.insert(logger, "testSave", testS, "testSave");
		// 业务保存成功
		if (result.isSuccess()) {
			isSucc = true;
		}
		return isSucc;
	}



	*//**
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
	 *//*
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		*//**
		 * 系统会存在两个容器，一个是root application context ,另一个就是我们自己的 projectName-servlet
		 * context（作为root application context的子容器）。
		 * 这种情况下，就会造成onApplicationEvent方法被执行两次。为了避免上面提到的问题，我们可以只在root
		 * application context初始化完成后调用逻辑代码，其他的容器的初始化完成，则不做任何处理，修改后代码
		 *//*
		if (event.getApplicationContext().getParent() == null) {
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			String queueName = "1111122222";
			// 开启客户端
			try {

				// 默认不开启事务
				Channel channel = connection.createChannel(false);

				// 绑定队列
				channel.queueDeclare(queueName, false, false, false, null);

				// 消费者模式
				Consumer consumer = new DefaultConsumer(channel) {
					@Override
					public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
							byte[] body) throws IOException {
						try {
							
							String message = new String(body, "UTF-8");
							JSONObject json=JSONObject.parseObject(message);
							String id=json.getString("id");
							String userId=json.getString("userId");
							boolean isSucc = testSave(userId);
							if (isSucc) {
								channel.basicAck(envelope.getDeliveryTag(), false);
								// 修改可靠消息的状态为已发送
								newService.updateNewsState(id, "2",null);
							}
						} catch (Exception e) {
							logger.error("[handleDelivery()->Exception:]", e);
						}

					}
				};

				// 设置false,消息不会默认ack
				channel.basicConsume(queueName, false, consumer);

			} catch (Exception e) {
				logger.error("[work()->Exception:]", e);
			}

		}

	}
}
*/