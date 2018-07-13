package com.mycom.admin;

import java.util.List;

import com.mycom.goods.GoodsModel;
import com.mycom.member.MemberModel;
import com.mycom.order.OrderModel;

public interface AdminDao {
	
	//상품리스트를 뽑아낸다.
	public List<GoodsModel> goodsList();
	//데이터를 넣는다.
	public Object insertGoods(GoodsModel GoodsModel);
	//키워드를 받아서 리스트를 받아온다.
	List<GoodsModel> goodsSearch0(String search);
	List<GoodsModel> goodsSearch1(String search);
	List<GoodsModel> goodsSearch2(String search);
	List<GoodsModel> goodsSearch3(String search);
	//goods_num를 받아서 삭제한다.
	public int goodsDelete(int goods_num);
	//수정폼을 받아서 수정한다.
	public int goodsModify(GoodsModel GoodsModel);
	//상세보기
	public GoodsModel goodsAdminView(int goods_num);
	//멤버 리스트를 뽑는다.
	public List<MemberModel> memberList();
	//키워드를 받아서 멤버를 찾는다.
	List<MemberModel> memberSearch0(String search);
	//멤버를 삭제한다. 
	public int memberDelete(String id);
	//수정폼을 받아서 수정을 한다.
	public Object adminmemberModify(MemberModel member);
	//주문리스트를 가져온다.
	public List<OrderModel> orderAllList();
	//키워드를 이용해 주문을 검색한다.
	public List<OrderModel> orderSearch0(String search);
	public List<OrderModel> orderSearch1(String search);
	public List<OrderModel> orderSearch2(String search);
	//주문 수정
	public Object orderModify(OrderModel OrderModel);
	
}


