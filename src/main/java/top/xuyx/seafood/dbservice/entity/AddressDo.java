package top.xuyx.seafood.dbservice.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("wx_address")
public class AddressDo implements Serializable {
	private static final long serialVersionUID = -2931900511400352598L;
	
	@TableId
	private String id;

	private String userId;

	private String name;

	private String mobile;

	private String addressArea;

	private String addressDetail;

	private Boolean lastUsed;

	private LocalDateTime createTime;

	private LocalDateTime updateTime;

}
