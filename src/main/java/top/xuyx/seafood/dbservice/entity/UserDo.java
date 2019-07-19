package top.xuyx.seafood.dbservice.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("wx_user")
public class UserDo implements Serializable {
	private static final long serialVersionUID = -2931900511400352598L;
	
	@TableId
	private String id;

	private String openid;

	private String name;

	private String avatar;

	private LocalDateTime createTime;

	private LocalDateTime updateTime;

}
