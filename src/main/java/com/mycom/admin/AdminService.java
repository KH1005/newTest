package com.mycom.admin;

import java.util.List;

import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;

import com.mycom.goods.GoodsModel;
import com.mycom.member.MemberModel;
import com.mycom.order.OrderModel;
@Service
public class AdminService implements AdminDao{
	@Resource(name="sqlSessionTemplate")		//의존주입을 한다. sqlSessionTemplate으로 정의되어있는 객체
	private SqlSessionTemplate sqlSessionTemplate;
	//AdminDao를 구현하여 오버라이딩한다. sqlSessionTemplate객체를 이용해서 디비와 연결한다.
	@Override
	public List<GoodsModel> goodsList(){
		return sqlSessionTemplate.selectList("goods.goodsList");
	}
	
	@Override
	public Object insertGoods(GoodsModel GoodsModel) {
		return sqlSessionTemplate.insert("goods.insertGoods", GoodsModel);
		
	}
	@Override
	public List<GoodsModel> goodsSearch0(String search) {
		return sqlSessionTemplate.selectList("goods.goodsSearch0", "%"+search+"%"); 
	}
	@Override
	public List<GoodsModel> goodsSearch1(String search) {
		return sqlSessionTemplate.selectList("goods.goodsSearch1", "%"+search+"%"); 
	}
	@Override
	public List<GoodsModel> goodsSearch2(String search) {
		return sqlSessionTemplate.selectList("goods.goodsSearch2", "%"+search+"%"); 
	}
	@Override
	public List<GoodsModel> goodsSearch3(String search) {
		return sqlSessionTemplate.selectList("goods.goodsSearch3", "%"+search+"%"); 
	}


	@Override
	public int goodsDelete(int goods_num) {
		return sqlSessionTemplate.delete("goods.goodsDelete",goods_num); 
	}
	
	@Override
	public int goodsModify(GoodsModel GoodsModel) {
		return sqlSessionTemplate.update("goods.goodsModify",GoodsModel); 
	}
	@Override
	public GoodsModel goodsAdminView(int goods_num){
		return sqlSessionTemplate.selectOne("goods.selectOne-goods",goods_num);
	}
	
	@Override
	public List<MemberModel> memberList() {
		return sqlSessionTemplate.selectList("member.memberList");
	}
	
	@Override
	public List<MemberModel> memberSearch0(String search) {
		return sqlSessionTemplate.selectList("member.memberSearch0", "%"+search+"%"); 
	}
	
	@Override
	public int memberDelete(String id) {
		return sqlSessionTemplate.delete("member.deleteMember",id);
	}
	
	@Override
	public Object adminmemberModify(MemberModel member) {
		return sqlSessionTemplate.update("member.adminupdateMember", member);
	}
	@Override
	public List<OrderModel> orderAllList() {
		return sqlSessionTemplate.selectList("order.orderAllList");
	}
	
	@Override
	public Object orderModify(OrderModel OrderModel) {
		return sqlSessionTemplate.update("order.orderModify", OrderModel);
	}
	
	@Override
	public List<OrderModel> orderSearch0(String search) {
		return sqlSessionTemplate.selectList("order.orderSearch0", "%"+search+"%"); 
	}
	
	@Override
	public List<OrderModel> orderSearch1(String search) {
		return sqlSessionTemplate.selectList("order.orderSearch1", "%"+search+"%"); 
	}
	
	
	@Override
	public List<OrderModel> orderSearch2(String search) {
		return sqlSessionTemplate.selectList("order.orderSearch2", "%"+search+"%"); 
	}
	
}
