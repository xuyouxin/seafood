package top.xuyx.seafood.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.xuyx.seafood.common.IdGenerator;
import top.xuyx.seafood.common.JsonUtil;
import top.xuyx.seafood.common.Response;
import top.xuyx.seafood.common.enums.StatusEnum;
import top.xuyx.seafood.dbservice.entity.AddressDo;
import top.xuyx.seafood.dbservice.mapper.AddressMapper;
import top.xuyx.seafood.dbservice.mapper.UserMapper;
import top.xuyx.seafood.model.in.RequestCommonModel;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private AddressMapper addressMapper;
	
	
	@PostMapping(value = "/add")
	public Response add(@RequestBody String json) {
		AddressDo address = JsonUtil.jsonToObject(json, AddressDo.class);
		if(address == null) {
			return Response.fail(StatusEnum.code_202);
		}
		if(StringUtils.isAnyBlank(address.getUserId(), address.getName(), address.getMobile(), address.getAddressArea(), address.getAddressDetail())) {
			return Response.fail(StatusEnum.code_204);
		}
		if(userMapper.selectById(address.getUserId())==null) {
			return Response.fail(StatusEnum.code_205);
		}
		address.setId(IdGenerator.generateId());
		int result = addressMapper.insert(address);
		if(result > 0) {
			return Response.ok();
		} else {
			return Response.fail(StatusEnum.code_201);
		}
	}
	
	//根据id删除
	@PostMapping(value = "/delete")
	public Response delete(@RequestBody String json) {
		AddressDo address = JsonUtil.jsonToObject(json, AddressDo.class);
		if(address == null) {
			return Response.fail(StatusEnum.code_202);
		}
		if(address.getId()==null) {
			return Response.fail(StatusEnum.code_204);
		}
		int result = addressMapper.deleteById(address.getId());
		if(result > 0) {
			return Response.ok();
		} else {
			return Response.fail(StatusEnum.code_201);
		}
	}
	
	//根据id更新
	@PostMapping(value="/update")
	public Response update(@RequestBody String json) {
		AddressDo address = JsonUtil.jsonToObject(json, AddressDo.class);
		if(address == null) {
			return Response.fail(StatusEnum.code_202);
		}
		if(StringUtils.isAnyBlank(address.getId(), address.getName(), address.getMobile(), address.getAddressArea(), address.getAddressDetail())) {
			return Response.fail(StatusEnum.code_204);
		}
		address.setUserId(null); //更新的时候不要更新用户id
		int result = addressMapper.updateById(address);
		if(result > 0) {
			return Response.ok();
		} else {
			return Response.fail(StatusEnum.code_201);
		}
	}
	
	@PostMapping(value="/select")
	public Response select(@RequestBody String json) {
		RequestCommonModel rcm = JsonUtil.jsonToObject(json, RequestCommonModel.class);
		if(rcm == null) {
			return Response.fail(StatusEnum.code_202);
		}
		if(rcm.getUserId()==null) {
			return Response.fail(StatusEnum.code_204);
		}
		if(userMapper.selectById(rcm.getUserId())==null) {
			return Response.fail(StatusEnum.code_205);
		}
		List<AddressDo> list = addressMapper.selectList(new EntityWrapper<AddressDo>()
				.eq("user_id", rcm.getUserId())
				.orderBy("update_time", false));
		return Response.ok(list);
	}
}
