package com.aop.log;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.aop.dto.UserDto;
import com.aop.dto.UserRegDto;
import com.aop.response.Response;

@Component
@Aspect
public class UserAudit {
	@Autowired
	private HttpServletRequest request;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserAudit.class);
	
	@Pointcut("execution(* com.aop.service..*.*(..))")
	public void pointCut() {};
	
	@Before("pointCut()")
	public void beforeUserCreate(JoinPoint joinPoint) {
		LOGGER.info("Before Method Start +++++++++++++++++++++++++++++++++++++");
		LOGGER.info("Before Method called by url :" +request.getRequestURL());
		LOGGER.info("method type :"+request.getMethod());
		LOGGER.info("User created with data :"+joinPoint.getArgs()[0]);
		LOGGER.info("Before Method End +++++++++++++++++++++++++++++++++++++++");
	}
	@Around("pointCut()")
	public Object getTotalProcessTime(ProceedingJoinPoint proceedingPoint) {
		LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		LOGGER.info("Around Method Start ========================================");
		LOGGER.info("Around Method called before createUser method get request");
		StopWatch watch=new StopWatch(proceedingPoint.getTarget().getClass().getName());
		Object ob=null;
			try {
				watch.start(proceedingPoint.getSignature().getName());
				ob=proceedingPoint.proceed();
				watch.stop();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			LOGGER.info(watch.prettyPrint());
			LOGGER.info("Around Method called after create user");
			LOGGER.info("Around Method End =========================================");
		return ob;
	}
	@AfterReturning(pointcut="execution(* com.aop.service..*.*(..))",returning="responce")
	public void afterReturningResponce(Response<UserRegDto> responce) {
		LOGGER.info("After Returning Start ****************************************************");
		LOGGER.info("user "+((UserRegDto)responce.getData()).getFirstName()+" "+((UserRegDto)responce.getData()).getLastName()+ " created by id "+((UserRegDto)responce.getData()).getUserId());
		LOGGER.info(responce.getMessage());
		LOGGER.info("After Returning Method callled when user get response");
		LOGGER.info("After Returning End ******************************************************");
		LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	}
	@After("pointCut()")
	public void afterUserCreated(JoinPoint joinPoint) {
		LOGGER.info("After Method Start -----------------------------------------------");
		LOGGER.info("user "+((UserDto)joinPoint.getArgs()[0]).getFirstName()+" "+((UserDto)joinPoint.getArgs()[0]).getLastName()+" created by user "+joinPoint.getArgs()[1] );
		LOGGER.info("After Method called before returning response");
		LOGGER.info("After Method End --------------------------------------------------");
	}
	}

