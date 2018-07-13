package com.mycom.admin;

/**
 * @author 
 * 
 * 
 * 
 * 
 * 
 */




import java.io.File;//asd
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.mycom.QnA.QnAModel;
import com.mycom.QnA.QnAService;
import com.mycom.goods.GoodsModel;
import com.mycom.goods.GoodsService;
import com.mycom.member.MemberModel;
import com.mycom.member.MemberService;
import com.mycom.notice.NoticeModel;
import com.mycom.notice.NoticeService;
import com.mycom.order.OrderModel;
import com.mycom.order.OrderService;
import com.mycom.pet.PetModel;
import com.mycom.pet.PetService;
import com.mycom.pet_img.Pet_imgModel;
import com.mycom.pet_img.Pet_imgService;
import com.mycom.review.ReviewModel;
import com.mycom.review.ReviewService;
import com.mycom.util.Paging;



@Controller	//컴포넌트 스캔을 등록해두었기때문에 자동으로 빈 객체로 만들어진다.
@RequestMapping("/admin")
public class AdminController {
	@Resource	//의존주입을 위한 resource어노테인션
	private AdminService adminService;
	
	@Resource
	private GoodsService goodsService;	//상품과 관련된 서비스를 쓰기위한 GoodsService의존주입
	
	@Resource
	private MemberService memberService;	//멤버와 관련된 서비스를 사용하기위한 의존주입
	
	@Resource
	private OrderService orderService;
	
	@Resource
	private NoticeService noticeService;
	
	@Resource
	private Pet_imgService pet_imgService;
	
	@Resource
	private PetService petService;
	
	@Resource
	private QnAService qnAService;
	
	@Resource
	private ReviewService reviewService;
	
	String uploadPath = "E:\\app3\\d_pro\\src\\main\\webapp\\resources\\goods_upload\\";
	
	//검색과 페이징을 위한 변수설정
	private int searchNum;
	private String isSearch;
	
	private int currentPage = 1;	 
	private int totalCount; 		 
	private int blockCount = 7;	 
	private int blockPage = 5; 	 
	private String pagingHtml;  
	private Paging page;		
	
	ModelAndView mav = new ModelAndView();
	//어드민 페이지를 보여준다.
	@RequestMapping(value="admin.dog")
	public String mainForm(){
		return "adminForm";
	}
	
	//goods admin 
	@RequestMapping("goodsadminList.dog")
	public ModelAndView goodsList(HttpServletRequest request) throws Exception{
		
		if(request.getParameter("currentPage") == null || request.getParameter("currentPage").trim().isEmpty() || request.getParameter("currentPage").equals("0")) {
            currentPage = 1;
        } else {
            currentPage = Integer.parseInt(request.getParameter("currentPage"));
        }
		
		//리스트를 얻어온다.
		List<GoodsModel> goodslist=adminService.goodsList();
		//검색어를 request로 받는다.
		isSearch = request.getParameter("isSearch");
		if(isSearch != null)	//검색어가 존재할 경우 
		{
			searchNum = Integer.parseInt(request.getParameter("searchNum"));

			if(searchNum==0)
				goodslist = adminService.goodsSearch0(isSearch);
			else if(searchNum == 1)
				goodslist = adminService.goodsSearch1(isSearch);
			else if(searchNum == 2)
				goodslist = adminService.goodsSearch2(isSearch);
			else if(searchNum == 3)
				goodslist = adminService.goodsSearch3(isSearch);
		
			totalCount = goodslist.size();
			page = new Paging(currentPage, totalCount, blockCount, blockPage, "goodsadminList", searchNum, isSearch);
			pagingHtml = page.getPagingHtml().toString();
		
			int lastCount = totalCount;
		
			if(page.getEndCount() < totalCount)
				lastCount = page.getEndCount() + 1;
			
			goodslist = goodslist.subList(page.getStartCount(), lastCount);
			//영역에 저장
			mav.addObject("isSearch", isSearch);
			mav.addObject("searchNum", searchNum);
			mav.addObject("totalCount", totalCount);
			mav.addObject("pagingHtml", pagingHtml);
			mav.addObject("currentPage", currentPage);
			mav.addObject("goodsList", goodslist);
			mav.setViewName("goodsadminList");
			return mav;
		}
		//검색어가 없을 경우
		totalCount = goodslist.size();
		
		page = new Paging(currentPage, totalCount, blockCount, blockPage, "goodsadminList");
		pagingHtml=page.getPagingHtml().toString(); 
		

		int lastCount = totalCount;
		 
		if (page.getEndCount() < totalCount)
			lastCount = page.getEndCount() + 1;
		 
		goodslist = goodslist.subList(page.getStartCount(), lastCount);
		
		mav.addObject("totalCount", totalCount);
		mav.addObject("pagingHtml", pagingHtml);
		mav.addObject("currentPage", currentPage);
		
		mav.addObject("goodsList", goodslist);
		mav.setViewName("goodsadminList");
		
		return mav;
	}
	
	//삽입폼을 보여준다.
	@RequestMapping("goodsInsertForm.dog")
	public ModelAndView goodsInsertForm(){
		
		mav.addObject("goods", new GoodsModel());
		mav.setViewName("admingoodsInsert");
		return mav;
	}
	
	//등록한다. 
	 @RequestMapping(value="goodsInsert.dog")    
	    public String insertGoods(MultipartHttpServletRequest multipartHttpServletRequest,GoodsModel GoodsModel) throws Exception {  //GoodsModel에 자동으로 들어가기, 업로드를 위한  MultipartHttpServletRequest
	 
	        System.out.println("UPLOAD_PATH : "+uploadPath);	
		        	
	        	MultipartFile multipartFile = multipartHttpServletRequest.getFile("file[0]");	//업로드
	        	String filename = multipartFile.getOriginalFilename();	//원본 파일이름을 얻는다.
		        	if (filename != ""){ 
					    GoodsModel.setGoods_image(System.currentTimeMillis()+"_"+multipartFile.getOriginalFilename());					    
					    String savimagename = System.currentTimeMillis()+"_"+multipartFile.getOriginalFilename();				    
				        FileCopyUtils.copy(multipartFile.getInputStream(), new FileOutputStream(uploadPath+"/"+savimagename));			            	        
		        	}else{
		        		GoodsModel.setGoods_image("NULL");		
		        	}
		        	
		        //contentimage
		        MultipartFile multipartFile1 = multipartHttpServletRequest.getFile("file[1]");
		        String filename1 = multipartFile1.getOriginalFilename();
		        	if (filename1 != ""){
					    GoodsModel.setGoods_contentimage(System.currentTimeMillis()+"_content"+multipartFile1.getOriginalFilename());					    
					    String savimagename1 = System.currentTimeMillis()+"_content"+multipartFile1.getOriginalFilename();				    
				        FileCopyUtils.copy(multipartFile1.getInputStream(), new FileOutputStream(uploadPath+"/"+savimagename1));
		        	}else{
		        		GoodsModel.setGoods_contentimage("NULL");		
		        	}
		        	
		      //delevimage 
		        MultipartFile multipartFile2 = multipartHttpServletRequest.getFile("file[2]");
		        String filename2 = multipartFile2.getOriginalFilename();
		        	if (filename2 != ""){
					    GoodsModel.setGoods_delevimage(System.currentTimeMillis()+"_delev"+multipartFile2.getOriginalFilename());					    
					    String savimagename2 = System.currentTimeMillis()+"_delev"+multipartFile2.getOriginalFilename();				    
				        FileCopyUtils.copy(multipartFile2.getInputStream(), new FileOutputStream(uploadPath+"/"+savimagename2));
		        	}else{
		        		GoodsModel.setGoods_delevimage("NULL");		
		        	}    
		       
				
				
	        
	        adminService.insertGoods(GoodsModel);
	        return "redirect:goodsadminList.dog";
	    }
	 
	 //물품 수정 폼
	 @RequestMapping("goodsModifyForm.dog")
		public ModelAndView noticeModifyForm(GoodsModel goodsModel, HttpServletRequest request){	//자동등록 
		 
			goodsModel = adminService.goodsAdminView(goodsModel.getGoods_num());	//서비스를 이용해 물품상세보기로 하나를 가져온다.
			
			//영역에 저장
			mav.addObject("goodsModel", goodsModel);	
			mav.setViewName("goodsModify");	//리턴할 파일
			
			return mav;	
		}
	 
	 //수정폼에서 적힌 데이터를 디비에 넣는다.
	 @RequestMapping("goodsModify.dog")
		public ModelAndView goodsModify(GoodsModel GoodsModel, MultipartHttpServletRequest multipartHttpServletRequest){//자동으로 빈에 저장, 업로드
		
		 	
	        System.out.println("UPLOAD_PATH : "+uploadPath);
		    
	        if (multipartHttpServletRequest.getFile("file[0]") != null){//첫 이미지를 업로드 하면
	        	MultipartFile multipartFile = multipartHttpServletRequest.getFile("file[0]"); 
	        	String filename = multipartFile.getOriginalFilename();//원본파일 이름을 얻는다. 
		        	if (filename != ""){	//파일이 존재한다면 
					    GoodsModel.setGoods_image(System.currentTimeMillis()+"_"+multipartFile.getOriginalFilename());	//이미존재하는 이름이 있다면 현제시간을 붙이고 파일이름을 만든다.					    
					    String savimagename = System.currentTimeMillis()+"_"+multipartFile.getOriginalFilename();					    
				        try {
							FileCopyUtils.copy(multipartFile.getInputStream(), new FileOutputStream(uploadPath+"/"+savimagename));//새로운 이름으로 파일을 올린다.
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}			            	        
		        	}
	        }else{	//이름이 존재하지 않으면 
	        	GoodsModel.setGoods_image(multipartHttpServletRequest.getParameter("goods_image"));
	        }
	        	//두번째 파일 업로드
	        if (multipartHttpServletRequest.getFile("file[1]") != null){
		        //contentimage
		        MultipartFile multipartFile1 = multipartHttpServletRequest.getFile("file[1]");
		        String filename1 = multipartFile1.getOriginalFilename();
		        	if (filename1 != ""){
					    GoodsModel.setGoods_contentimage(System.currentTimeMillis()+"_content"+multipartFile1.getOriginalFilename());					    
					    String savimagename1 = System.currentTimeMillis()+"_content"+multipartFile1.getOriginalFilename();				    
				        try {
							FileCopyUtils.copy(multipartFile1.getInputStream(), new FileOutputStream(uploadPath+"/"+savimagename1));
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		        	}
	        }else{
	        	GoodsModel.setGoods_contentimage(multipartHttpServletRequest.getParameter("goods_contentimage"));
	        }
	        
	        //세번째 파일 업로드
	        if (multipartHttpServletRequest.getFile("file[2]") != null){
		        MultipartFile multipartFile2 = multipartHttpServletRequest.getFile("file[2]");
		        String filename2 = multipartFile2.getOriginalFilename();
		        	if (filename2 != ""){
					    GoodsModel.setGoods_delevimage(System.currentTimeMillis()+"_delev"+multipartFile2.getOriginalFilename());					    
					    String savimagename2 = System.currentTimeMillis()+"_delev"+multipartFile2.getOriginalFilename();				    
				        try {
							FileCopyUtils.copy(multipartFile2.getInputStream(), new FileOutputStream(uploadPath+"/"+savimagename2));
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
		        	} 
	        }else{
	        	GoodsModel.setGoods_delevimage(multipartHttpServletRequest.getParameter("goods_delevimage"));
	        }		
			//수정한걸 디비에 저장
	        adminService.goodsModify(GoodsModel);
	        //영역에 저장
			mav.addObject("goodsModel", GoodsModel);
  			mav.setViewName("redirect:goodsadminList.dog");	//리다이렉트 시킨다.
  			return mav;
		}
	 
	 //삭제
	 @RequestMapping("goodsDelete.dog")
		public ModelAndView goodsDelete(HttpServletRequest request, GoodsModel GoodsModel){
		 int goods_num = Integer.parseInt(request.getParameter("goods_num"));//물품번호를 받는다.
		 GoodsModel = goodsService.goodsView(goods_num);	//상세보기를 한다.
		 
		 String filename = GoodsModel.getGoods_image();	
		 String filename1 = GoodsModel.getGoods_contentimage();
		 String filename2 = GoodsModel.getGoods_delevimage();
		 System.out.println(filename);	//파일들을 가져온다.
		 
		 //업로드된 파일들을 모두 삭제하기 위해 상세보기로 하나 불러와서 임시파일 f를 이용해 업로드된 파일들을 삭제한다. 
		 File f = new File(uploadPath + filename);	//새로운파일을 만든다. 
		 System.out.println(f.isFile());
		 if(f.exists()){
			 f.delete();	//삭제
			 System.out.println("goods_image ���� ����");
		 }else{
			 System.out.println("goods_image ���Ͼ���");
		 }
		 
		 f = new File(uploadPath + filename1);	
		 if(f.exists()){
			 f.delete();
			 System.out.println("getGoods_contentimage ���� ����");
		 }else{
			 System.out.println("getGoods_contentimage ���Ͼ���");
		 }
		 
		 f = new File(uploadPath + filename2);
		 if(f.exists()){
			 f.delete();
			 System.out.println("getGoods_delevimage ���� ����");
		 }else{
			 System.out.println("getGoods_delevimage ���Ͼ���");
		 }
		 
		 
		 adminService.goodsDelete(goods_num);	//업로드파일을 모두 삭제했으면 디비값을 삭제한다.
		 mav.setViewName("redirect:goodsadminList.dog");
			
		 return mav;	
	}
	 	
	 	//멤버 리스트를 불러온다.
		@RequestMapping("memberadminList.dog")
		public ModelAndView memberList(HttpServletRequest request) throws Exception{
			
			if(request.getParameter("currentPage") == null || request.getParameter("currentPage").trim().isEmpty() || request.getParameter("currentPage").equals("0")) {
	            currentPage = 1;
	        } else {
	            currentPage = Integer.parseInt(request.getParameter("currentPage"));
	        }
			
			//서비스객체를 이용해 멤버리스트를 가져온다.
			List<MemberModel> memberlist=adminService.memberList();
			
			//검색어를 가져온다.
			isSearch = request.getParameter("isSearch");
			//검색어가 존재한다면
			if(isSearch != null)
			{
				searchNum = Integer.parseInt(request.getParameter("searchNum"));

				if(searchNum==0)
					memberlist = adminService.memberSearch0(isSearch);
			
				totalCount = memberlist.size();
				//페이징을 위한 page객체를 생성하고
				page = new Paging(currentPage, totalCount, blockCount, blockPage, "memberadminList", searchNum, isSearch);
				pagingHtml = page.getPagingHtml().toString();	//페이징값을 저장한다.
			
				int lastCount = totalCount;
			
				if(page.getEndCount() < totalCount)
					lastCount = page.getEndCount() + 1;
				//페이징을 디비에서부터 하는게 아닌 모든 디비리스트를 가져와서 자바코드를 이용해 페이징을 한다. 
				memberlist = memberlist.subList(page.getStartCount(), lastCount);
				//영역에 저장한다.
				mav.addObject("isSearch", isSearch);
				mav.addObject("searchNum", searchNum);
				mav.addObject("totalCount", totalCount);
				mav.addObject("pagingHtml", pagingHtml);
				mav.addObject("currentPage", currentPage);
				mav.addObject("memberlist", memberlist);
				mav.setViewName("memberadminList");
				return mav;
			}
			//검색어가 존재하지 않을때 리스트를 가져온다. 
			totalCount = memberlist.size();
			
			page = new Paging(currentPage, totalCount, blockCount, blockPage, "memberadminList");
			pagingHtml=page.getPagingHtml().toString(); 
			

			int lastCount = totalCount;
			 
			if (page.getEndCount() < totalCount)
				lastCount = page.getEndCount() + 1;
			 
			memberlist = memberlist.subList(page.getStartCount(), lastCount);
			
			mav.addObject("totalCount", totalCount);
			mav.addObject("pagingHtml", pagingHtml);
			mav.addObject("currentPage", currentPage);
			
			mav.addObject("memberlist", memberlist);
			mav.setViewName("memberadminList");
			
			return mav;
		}
		//관리자 멤버 삭제
		 @RequestMapping("adminMemberDelete.dog")
			public ModelAndView memberDelete(HttpServletRequest request){		
			 String id = request.getParameter("id");	//아이디를 얻어와서
			 adminService.memberDelete(id);	//삭제
			 mav.setViewName("redirect:memberadminList.dog");	//리다이렉트
				
			 return mav;	
		}
		//멤버 수정 
	  	@RequestMapping("adminmemberModify.dog")
	  	public ModelAndView memberModify(MemberModel member, HttpServletRequest request) {//멤버수정폼에서 저장된 데이터들을 자동으로 자바빈에 저장한다.  		  		 		
	  		
  			member =  memberService.getMember(member.getId());	//멤버를 가져온다. 
  	
  			mav.addObject("member", member); //멤버폼을 채우기위해 영역에 저장한다.
  			mav.setViewName("memberadminModify");	//포워드
  			return mav;
	  	}
	  	//수정완료
	    @RequestMapping("adminmemberModifyEnd.dog")
  		public ModelAndView adminmemberModifyEnd(MemberModel member) {	//수정폼에서 적힌 내용들을 자동으로 저장한다.
  		
		System.out.println("��������");
		
  			adminService.adminmemberModify(member);		//서비스를 이용해 디비 값을 수정한다. 
  			mav.setViewName("redirect:memberadminList.dog"); //리다이렉트
  			return mav;
	    }
	    
	    
	    //�ֹ�����Ʈ
	    @RequestMapping("adminOrderAllList.dog")
		public ModelAndView OrderList(HttpServletRequest request) throws Exception{
			
			if(request.getParameter("currentPage") == null || request.getParameter("currentPage").trim().isEmpty() || request.getParameter("currentPage").equals("0")) {
	            currentPage = 1;
	        } else {
	            currentPage = Integer.parseInt(request.getParameter("currentPage"));
	        }
			
			List<OrderModel> orderlist=adminService.orderAllList();
			
			String isSearch = request.getParameter("isSearch");
			if(isSearch != null) isSearch = new String(isSearch.getBytes("8859_1"), "UTF-8");
			
			
			if(isSearch != null)
			{
				searchNum = Integer.parseInt(request.getParameter("searchNum"));

				if(searchNum==0)//��ü
					orderlist = adminService.orderSearch0(isSearch);
				else if(searchNum==1)//������
					orderlist = adminService.orderSearch1(isSearch);
				else if(searchNum==2)//�ֹ�����
					orderlist = adminService.orderSearch2(isSearch);
			
				totalCount = orderlist.size();
				page = new Paging(currentPage, totalCount, blockCount, blockPage, "adminOrderAllList", searchNum, isSearch);
				pagingHtml = page.getPagingHtml().toString();
			
				int lastCount = totalCount;
			
				if(page.getEndCount() < totalCount)
					lastCount = page.getEndCount() + 1;
				
				orderlist = orderlist.subList(page.getStartCount(), lastCount);
			
				mav.addObject("isSearch", isSearch);
				mav.addObject("searchNum", searchNum);
				mav.addObject("totalCount", totalCount);
				mav.addObject("pagingHtml", pagingHtml);
				mav.addObject("currentPage", currentPage);
				mav.addObject("orderList", orderlist);
				mav.setViewName("orderAllList");
				return mav;
			}
			
			totalCount = orderlist.size();
			
			page = new Paging(currentPage, totalCount, blockCount, blockPage, "adminOrderAllList");
			pagingHtml=page.getPagingHtml().toString(); 
			

			int lastCount = totalCount;
			 
			if (page.getEndCount() < totalCount)
				lastCount = page.getEndCount() + 1;
			 
			orderlist = orderlist.subList(page.getStartCount(), lastCount);
			
			mav.addObject("totalCount", totalCount);
			mav.addObject("pagingHtml", pagingHtml);
			mav.addObject("currentPage", currentPage);
			
			mav.addObject("orderList", orderlist);
			mav.setViewName("orderAllList");
			
			return mav;
		}
	    
	  //�ֹ� �����ϱ� ��
		 @RequestMapping("orderModifyForm.dog")
		public ModelAndView orderModifyForm(OrderModel orderModel, HttpServletRequest request){
			
		 
			orderModel = orderService.OrdergetOne(orderModel.getOrder_num());
			
			mav.addObject("orderModel", orderModel);
			mav.setViewName("orderModify");
			
			return mav;	
		}
		// �ֹ�����
		  	@RequestMapping("orderModify.dog")
		  	public ModelAndView orderModify(OrderModel OrderModel, HttpServletRequest request) {		  		 		
		  		
		 
		  	
		  		adminService.orderModify(OrderModel);
	  	
	  			mav.setViewName("redirect:adminOrderAllList.dog");
	  			return mav;
		  	}
		  	/*public ModelAndView adminmemberModifyEnd(MemberModel member) {
		  		
				System.out.println("��������");
				
		  			adminService.adminmemberModify(member);
		  			mav.setViewName("redirect:memberadminList.dog");
		  			return mav;
			    }*/
	    
	  //�ֹ���� ����
        @RequestMapping(value="orderadmindelete.dog")
        public ModelAndView orderdelete(HttpServletRequest request, OrderModel orderModel){
        	
        	orderService.deleteOrder(orderModel);
        	
        	mav.setViewName("redirect:adminOrderAllList.dog"); 
        	return mav;
        }
        
        
        
       //�Խ��ǰ���/////////////////////////////////
       //��������//
        @RequestMapping(value="adminnoticeList.dog", method=RequestMethod.GET)
    	public ModelAndView noticeList(HttpServletRequest request){
    		
    		ModelAndView mav = new ModelAndView();
    		
    		if(request.getParameter("currentPage") == null || request.getParameter("currentPage").trim().isEmpty() || request.getParameter("currentPage").equals("0")) {
                currentPage = 1;
            } else {
                currentPage = Integer.parseInt(request.getParameter("currentPage"));
            }

    		List<NoticeModel> noticeList;
    		
    		isSearch = request.getParameter("isSearch");
    		if(isSearch != null)
    		{
    			searchNum = Integer.parseInt(request.getParameter("searchNum"));

    			if(searchNum==0)
    				noticeList = noticeService.noticeSearch0(isSearch);
    			else if(searchNum==1)
    				noticeList = noticeService.noticeSearch1(isSearch);
    			else /*if(searchN==2)*/
    				noticeList = noticeService.noticeSearch2(isSearch);
    		
    			totalCount = noticeList.size();
    			page = new Paging(currentPage, totalCount, blockCount, blockPage, "adminnoticeList", searchNum, isSearch);
    			pagingHtml = page.getPagingHtml().toString();
    		
    			int lastCount = totalCount;
    		
    			if(page.getEndCount() < totalCount)
    				lastCount = page.getEndCount() + 1;
    			
    			noticeList = noticeList.subList(page.getStartCount(), lastCount);
    		
    			mav.addObject("isSearch", isSearch);
    			mav.addObject("searchNum", searchNum);
    			mav.addObject("totalCount", totalCount);
    			mav.addObject("pagingHtml", pagingHtml);
    			mav.addObject("currentPage", currentPage);
    			mav.addObject("noticeList", noticeList);
    			mav.setViewName("noticeadminList");
    			return mav;
    		}
    		
    		noticeList = noticeService.noticeList();
    		
    		totalCount = noticeList.size();
    		
    		page = new Paging(currentPage, totalCount, blockCount, blockPage, "adminnoticeList");
    		pagingHtml=page.getPagingHtml().toString();  
    		
    		int lastCount = totalCount;
    		 
    		if (page.getEndCount() < totalCount)
    			lastCount = page.getEndCount() + 1;
    		 
    		noticeList = noticeList.subList(page.getStartCount(), lastCount);
    		
    		mav.addObject("totalCount", totalCount);
    		mav.addObject("pagingHtml", pagingHtml);
    		mav.addObject("currentPage", currentPage);
    		mav.addObject("noticeList", noticeList);
    		mav.setViewName("noticeadminList");
    		return mav;
    	}
        
        //�������׻���
        @RequestMapping("adminnoticeDelete.dog")
    	public ModelAndView noticeDelete(HttpServletRequest request){
    		
    		ModelAndView mav = new ModelAndView();
    		int no = Integer.parseInt(request.getParameter("no"));
    		noticeService.noticeDelete(no);
    		mav.setViewName("redirect:adminnoticeList.dog");
    		
    		return mav;	
    	}
        
        //�����긮��Ʈ
        @RequestMapping("adminpet_imgList.dog")
    	public String pet_imgList(Model model,Pet_imgModel pet_imgModel, HttpServletRequest request ){
    		
    		
        	List<Pet_imgModel> pet_imgList;
        	
    		if(request.getParameter("currentPage") == null || request.getParameter("currentPage").trim().isEmpty() || request.getParameter("currentPage").equals("0")) {
                currentPage = 1;
            } else {
                currentPage = Integer.parseInt(request.getParameter("currentPage"));
            }
    		
    		isSearch = request.getParameter("isSearch");
    		
    		if(isSearch != null)
    		{
    			searchNum = Integer.parseInt(request.getParameter("searchNum"));
    			
    			
    			if(searchNum==0)
    				pet_imgList = pet_imgService.pet_imgSearch0(isSearch);
    			else if(searchNum==1)
    				pet_imgList = pet_imgService.pet_imgSearch1(isSearch);
    			else /*if(searchN==2)*/
    				pet_imgList = pet_imgService.pet_imgSearch2(isSearch);
    			
    			
    			
    			totalCount = pet_imgList.size();
    			
    			
    			page = new Paging(currentPage, totalCount, blockCount, blockPage, "adminpet_imgList", searchNum, isSearch);
    			pagingHtml = page.getPagingHtml().toString();
    		
    			int lastCount = totalCount;
    		
    			if(page.getEndCount() < totalCount)
    				lastCount = page.getEndCount() + 1;
    			
    			pet_imgList = pet_imgList.subList(page.getStartCount(), lastCount);
    		
    			
    			model.addAttribute("isSearch", isSearch);
    			model.addAttribute("searchNum", searchNum);
    			model.addAttribute("totalCount", totalCount);
    			model.addAttribute("pagingHtml", pagingHtml);
    			model.addAttribute("currentPage", currentPage);
    			model.addAttribute("list", pet_imgList);
    			
    			return "adminpet_imgList";
    		}
    		
    		
    		
    		pet_imgList = pet_imgService.pet_imgGetList(pet_imgModel);
    		
    		totalCount = pet_imgList.size();
    		
    		page = new Paging(currentPage, totalCount, blockCount, blockPage, "adminpet_imgList");
    		pagingHtml=page.getPagingHtml().toString();  
    		
    		int lastCount = totalCount;
    		 
    		if (page.getEndCount() < totalCount)
    			lastCount = page.getEndCount() + 1;
    		 
    		pet_imgList = pet_imgList.subList(page.getStartCount(), lastCount);
    		
    		model.addAttribute("totalCount", totalCount);
    		model.addAttribute("pagingHtml", pagingHtml);
    		model.addAttribute("currentPage", currentPage);
    		
    		model.addAttribute("list", pet_imgList);
    		
    		return "adminpet_imgList";
    	}
        
        //������ ����
        @RequestMapping("adminpet_imgDelete.dog")
    	public ModelAndView pet_imgDelete(HttpServletRequest request, Pet_imgModel pet_imgModel ){
    	
    		ModelAndView mav=new ModelAndView();
    	
    		pet_imgService.deletePet_img(pet_imgModel);
    	
    		mav.setViewName("redirect:adminpet_imgList.dog");
    	
    		return mav;
    	
        }
        
        
        //�о�Խ��� ����Ʈ
        @RequestMapping(value="adminpetList.dog")
    	public String petList(Model model,PetModel petModel, HttpServletRequest request ){
    		
        	List<PetModel> petList;
    		
    		if(request.getParameter("currentPage") == null || request.getParameter("currentPage").trim().isEmpty() || request.getParameter("currentPage").equals("0")) {
                currentPage = 1;
            } else {
                currentPage = Integer.parseInt(request.getParameter("currentPage"));
            }
    		
    		
    		
    		
    		isSearch = request.getParameter("isSearch");
    		
    		if(isSearch != null)
    		{
    			searchNum = Integer.parseInt(request.getParameter("searchNum"));
    			
    			
    			if(searchNum==0)
    				petList = petService.petSearch0(isSearch);
    			else if(searchNum==1)
    				petList = petService.petSearch1(isSearch);
    			else /*if(searchN==2)*/
    				petList = petService.petSearch2(isSearch);
    			
    			
    			
    			totalCount = petList.size();
    			
    			
    			page = new Paging(currentPage, totalCount, blockCount, blockPage, "adminpetList", searchNum, isSearch);
    			pagingHtml = page.getPagingHtml().toString();
    		
    			int lastCount = totalCount;
    		
    			if(page.getEndCount() < totalCount)
    				lastCount = page.getEndCount() + 1;
    			
    			petList = petList.subList(page.getStartCount(), lastCount);
    		
    			
    			model.addAttribute("isSearch", isSearch);
    			model.addAttribute("searchNum", searchNum);
    			model.addAttribute("totalCount", totalCount);
    			model.addAttribute("pagingHtml", pagingHtml);
    			model.addAttribute("currentPage", currentPage);
    			model.addAttribute("list", petList);
    			
    			return "adminpetList";
    		}
    		
    		
    		
    		petList = petService.petGetList(petModel);
    		
    		totalCount = petList.size();
    		
    		page = new Paging(currentPage, totalCount, blockCount, blockPage, "adminpetList");
    		pagingHtml=page.getPagingHtml().toString();  
    		
    		int lastCount = totalCount;
    		 
    		if (page.getEndCount() < totalCount)
    			lastCount = page.getEndCount() + 1;
    		 
    		petList = petList.subList(page.getStartCount(), lastCount);
    		
    		model.addAttribute("totalCount", totalCount);
    		model.addAttribute("pagingHtml", pagingHtml);
    		model.addAttribute("currentPage", currentPage);
    		
    		model.addAttribute("list", petList);
    		
    		return "adminpetList";
    	}
        //�о�Խ��� ����
        @RequestMapping("adminpetDelete.dog")
    	public ModelAndView petDelete(HttpServletRequest request, PetModel petModel ){
    	
    		ModelAndView mav=new ModelAndView();
    	
    		petService.deletePet(petModel);
    	
    		mav.setViewName("redirect:adminpetList.dog");
    	
    		return mav;
    	
      }
        //Qna����Ʈ
        @RequestMapping(value="adminQnAList.dog")
    	public ModelAndView qnaList(HttpServletRequest request, QnAModel qnaModel){
    		ModelAndView mav = new ModelAndView();

    		if(request.getParameter("currentPage") == null || request.getParameter("currentPage").trim().isEmpty() || request.getParameter("currentPage").equals("0")) {
                currentPage = 1;
            } else {
                currentPage = Integer.parseInt(request.getParameter("currentPage"));
            }
    		
    		int commupdate1;
    		List<QnAModel> list;
    		list = qnAService.QnAList();
    		
    		isSearch = request.getParameter("isSearch");
    		
    		if(isSearch != null)		{
    			searchNum = Integer.parseInt(request.getParameter("searchNum"));
    			
    			if(searchNum==0){
    				list = qnAService.QnASearch0(isSearch);
    			}else if(searchNum==1){
    				list =  qnAService.QnASearch1(isSearch);
    			}else if(searchNum==2){
    				list =  qnAService.QnASearch2(isSearch);
    			}
    		}
    			
    		totalCount = list.size();
    		page = new Paging(currentPage, totalCount, blockCount, blockPage, "adminQnAList");
    		pagingHtml=page.getPagingHtml().toString();  
    		
    		int lastCount = totalCount;
    		
    		if (page.getEndCount() < totalCount){
    			lastCount = page.getEndCount() + 1;}
    			
    		list = list.subList(page.getStartCount(), lastCount);

    		int no = qnaModel.getNo();
    		commupdate1 = qnAService.QnAcommUpdate1(no);
    		
    		mav.addObject("QnAModel", qnaModel);
    		mav.setViewName("QnAView");

    		mav.addObject("isSearch", isSearch);
    		mav.addObject("searchNum", searchNum);
    		mav.addObject("totalCount", totalCount);
    		mav.addObject("pagingHtml", pagingHtml);
    		mav.addObject("currentPage", currentPage);
    		mav.addObject("list", list);
    		mav.addObject("commupdate1",commupdate1);
    		mav.setViewName("adminQnAList");
    		
    		return mav;
    	}
        
        //qna����
        @RequestMapping(value="adminQnADelete.dog")
    	public ModelAndView qnaDelete(HttpServletRequest request, QnAModel qnaModel){			   
    			ModelAndView mav = new ModelAndView();
    			
    			
    			qnAService.QnADelete(qnaModel.getNo());
    			qnAService.QnAallcommDelete(qnaModel.getNo());
    			mav.setViewName("redirect:adminQnAList.dog");
    			
    			return mav;
    	}
        
        //�����ı� ����Ʈ
    	@RequestMapping(value="adminreviewList.dog")
    	public ModelAndView reviewList(HttpServletRequest request){
    		
    		ModelAndView mav = new ModelAndView();
    		
    		if(request.getParameter("currentPage") == null || request.getParameter("currentPage").trim().isEmpty() || request.getParameter("currentPage").equals("0")) {
                currentPage = 1;
            } else {
                currentPage = Integer.parseInt(request.getParameter("currentPage"));
            }
    		
    		List<ReviewModel> reviewList = null;
    		
    		isSearch = request.getParameter("isSearch");
    		
    		if(isSearch != null)
    		{
    			searchNum = Integer.parseInt(request.getParameter("searchNum"));
    			
    			if(searchNum==0)
    				reviewList = reviewService.reviewSearch0(isSearch);
    			else if(searchNum==1)
    				reviewList = reviewService.reviewSearch1(isSearch);
    			else if(searchNum==2)
    				reviewList = reviewService.reviewSearch2(isSearch);
    		
    			totalCount = reviewList.size();
    			page = new Paging(currentPage, totalCount, blockCount, blockPage, "adminreviewList", searchNum, isSearch);
    			pagingHtml = page.getPagingHtml().toString();
    		
    			int lastCount = totalCount;
    		
    			if(page.getEndCount() < totalCount)
    				lastCount = page.getEndCount() + 1;
    			
    			reviewList = reviewList.subList(page.getStartCount(), lastCount);
    		
    			mav.addObject("isSearch", isSearch);
    			mav.addObject("searchNum", searchNum);
    			mav.addObject("totalCount", totalCount);
    			mav.addObject("pagingHtml", pagingHtml);
    			mav.addObject("currentPage", currentPage);
    			mav.addObject("reviewList", reviewList);
    			mav.setViewName("adminreviewList");
    			return mav;
    		}
    		
    		reviewList = reviewService.reviewList();
    		
    		totalCount = reviewList.size();
    		
    		page = new Paging(currentPage, totalCount, blockCount, blockPage, "adminreviewList");
    		pagingHtml=page.getPagingHtml().toString();
    		
    		int lastCount = totalCount;
    		 
    		if (page.getEndCount() < totalCount)
    			lastCount = page.getEndCount() + 1;
    		 
    		reviewList = reviewList.subList(page.getStartCount(), lastCount);
    		
    		mav.addObject("totalCount", totalCount);
    		mav.addObject("pagingHtml", pagingHtml);
    		mav.addObject("currentPage", currentPage);
    		mav.addObject("reviewList", reviewList);
    		mav.setViewName("adminreviewList");
    		return mav;
    	}
    	
    	//�����ı� ����
    	@RequestMapping("adminreviewDelete.dog")
    	public ModelAndView reviewDelete(HttpServletRequest request){
    		
    		ModelAndView mav = new ModelAndView();
    		int no = Integer.parseInt(request.getParameter("no"));
    		reviewService.reviewDelete(no);
    		mav.setViewName("redirect:adminreviewList.dog");
    		
    		return mav;	
    	}
    	
	
}
