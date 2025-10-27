package com.example.lease.web.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.lease.common.exception.LeaseException;
import com.example.lease.common.result.ResultCodeEnum;
import com.example.lease.model.entity.*;
import com.example.lease.model.enums.ItemType;
import com.example.lease.web.admin.mapper.*;
import com.example.lease.web.admin.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lease.web.admin.vo.apartment.ApartmentDetailVo;
import com.example.lease.web.admin.vo.apartment.ApartmentItemVo;
import com.example.lease.web.admin.vo.apartment.ApartmentQueryVo;
import com.example.lease.web.admin.vo.apartment.ApartmentSubmitVo;
import com.example.lease.web.admin.vo.fee.FeeValueVo;
import com.example.lease.web.admin.vo.graph.GraphVo;
import kotlin.jvm.internal.Lambda;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.ParameterResolutionDelegate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【apartment_info(公寓信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
        implements ApartmentInfoService {

    @Autowired
    private FeeValueMapper feeValueMapper;

    @Autowired
    private FacilityInfoService facilityInfoService;

   @Autowired
   private FacilityInfoMapper facilityInfoMapper;
    @Autowired
    private LabelInfoMapper labelInfoMapper;
    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;
    @Autowired
    private GraphInfoService graphInfoService;

    @Autowired
    private GraphInfoMapper graphInfoMapper;

    @Autowired
    private ApartmentFacilityService apartmentFacilityService;

    @Autowired
    private ApartmentFeeValueService apartmentFeeValueService;

    @Autowired
    private ApartmentLabelService apartmentLabelService;

    @Autowired
    private RoomInfoMapper roomInfoMapper;

    @Override
    public void saveOrUpdateApartment(ApartmentSubmitVo apartmentSubmitVo) {
        super.saveOrUpdate(apartmentSubmitVo);
        boolean isUpdate=apartmentSubmitVo.getId()!=null;
        if(isUpdate){
            //1.删除图片列表
            LambdaQueryWrapper<GraphInfo> graphQueryWrapper = new LambdaQueryWrapper<>();
            graphQueryWrapper.eq(GraphInfo::getItemType, ItemType.APARTMENT);
            graphQueryWrapper.eq(GraphInfo::getItemId,apartmentSubmitVo.getId());
            graphInfoService.remove(graphQueryWrapper);
            //2.删除配套列表
            LambdaQueryWrapper<ApartmentFacility> facilityQueryWrapper = new LambdaQueryWrapper<>();
            facilityQueryWrapper.eq(ApartmentFacility::getApartmentId,apartmentSubmitVo.getId());
            //3.删除标签列表
            LambdaQueryWrapper<ApartmentLabel> labelQueryWrapper = new LambdaQueryWrapper<>();
            labelQueryWrapper.eq(ApartmentLabel::getApartmentId,apartmentSubmitVo.getId());
            //4.删除杂费列表
            LambdaQueryWrapper<ApartmentFeeValue> feeQueryWrapper = new LambdaQueryWrapper<>();
            feeQueryWrapper.eq(ApartmentFeeValue::getApartmentId,apartmentSubmitVo.getId());
            apartmentFeeValueService.remove(feeQueryWrapper);
        }
        //1.插入图片列表
        List<GraphVo> graphVoList = apartmentSubmitVo.getGraphVoList();
        ArrayList<GraphInfo> graphInfoList = new ArrayList<>();
        for(GraphVo graphVo:graphVoList){
            GraphInfo graphInfo=new GraphInfo();
            graphInfo.setItemType(ItemType.APARTMENT);
            graphInfo.setItemId(apartmentSubmitVo.getId());
            graphInfo.setName(graphVo.getName());
            graphInfo.setUrl(graphVo.getUrl());
        }
        graphInfoService.saveBatch(graphInfoList);

        //2.插入配套列表
        List<Long> facilityInfoIdList = apartmentSubmitVo.getFacilityInfoIds();
        ArrayList<ApartmentFacility>facilityList=new ArrayList<>();
        for(Long facilityId:facilityInfoIdList){
            ApartmentFacility apartmentFacility=new ApartmentFacility();
            apartmentFacility.setApartmentId(apartmentSubmitVo.getId());
            apartmentFacility.setFacilityId(facilityId);
            facilityList.add(apartmentFacility);
        }
        apartmentFacilityService.saveBatch(facilityList);
        //3.插入标签列表
        List<Long> labelIds = apartmentSubmitVo.getLabelIds();
        if (!CollectionUtils.isEmpty(labelIds)) {
            List<ApartmentLabel> apartmentLabelList = new ArrayList<>();
            for (Long labelId : labelIds) {
                ApartmentLabel apartmentLabel = new ApartmentLabel();
                apartmentLabel.setApartmentId(apartmentSubmitVo.getId());
                apartmentLabel.setLabelId(labelId);
                apartmentLabelList.add(apartmentLabel);
            }
            apartmentLabelService.saveBatch(apartmentLabelList);
        }


        //4.插入杂费列表
        List<Long> feeValueIds = apartmentSubmitVo.getFeeValueIds();
        if (!CollectionUtils.isEmpty(feeValueIds)) {
            ArrayList<ApartmentFeeValue> apartmentFeeValueList = new ArrayList<>();
            for (Long feeValueId : feeValueIds) {
                ApartmentFeeValue apartmentFeeValue = new ApartmentFeeValue();
                apartmentFeeValue.setApartmentId(apartmentSubmitVo.getId());
                apartmentFeeValue.setFeeValueId(feeValueId);
                apartmentFeeValueList.add(apartmentFeeValue);
            }
            apartmentFeeValueService.saveBatch(apartmentFeeValueList);
        }
    }
    @Override
    public IPage<ApartmentItemVo> pageItem(Page<ApartmentItemVo> page, ApartmentQueryVo queryVo){
         return apartmentInfoMapper.pageItem(page,queryVo);
    }

    @Override
    public ApartmentDetailVo getApartmentDetailById(Long id) {
        //1.查询ApartmentInfo
        ApartmentInfo apartmentInfo = this.getById(id);
        if (apartmentInfo == null) {
            return null;
        }

        //2.查询GraphInfo
        List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.APARTMENT, id);

        //3.查询LabelInfo
        List<LabelInfo> labelInfoList = labelInfoMapper.selectListByApartmentId(id);

        //4.查询FacilityInfo
        List<FacilityInfo> facilityInfoList = facilityInfoMapper.selectListByApartmentId(id);

        //5.查询FeeValue
        List<FeeValueVo> feeValueVoList = feeValueMapper.selectListByApartmentId(id);

        ApartmentDetailVo adminApartmentDetailVo = new ApartmentDetailVo();

        BeanUtils.copyProperties(apartmentInfo, adminApartmentDetailVo);
        adminApartmentDetailVo.setGraphVoList(graphVoList);
        adminApartmentDetailVo.setLabelInfoList(labelInfoList);
        adminApartmentDetailVo.setFacilityInfoList(facilityInfoList);
        adminApartmentDetailVo.setFeeValueVoList(feeValueVoList);

        return adminApartmentDetailVo;
    }

    @Override
    public void removeApartmentId(Long id) {
        LambdaQueryWrapper<RoomInfo> roomQueryWrapper = new LambdaQueryWrapper<>();
        roomQueryWrapper.eq(RoomInfo::getApartmentId,id);
        Long count=roomInfoMapper.selectCount(roomQueryWrapper);

        if(count>0){
            //终止删除，并且返回提示信息
            throw new LeaseException(ResultCodeEnum.ADMIN_APARTMENT_DELETE_ERROR);
        }

        super.removeById(id);
        //1.删除图片列表
        LambdaQueryWrapper<GraphInfo> graphQueryWrapper = new LambdaQueryWrapper<>();
        graphQueryWrapper.eq(GraphInfo::getItemType, ItemType.APARTMENT);
        graphQueryWrapper.eq(GraphInfo::getItemId,id);
        graphInfoService.remove(graphQueryWrapper);
        //2.删除配套列表
        LambdaQueryWrapper<ApartmentFacility> facilityQueryWrapper = new LambdaQueryWrapper<>();
        facilityQueryWrapper.eq(ApartmentFacility::getApartmentId,id);
        //3.删除标签列表
        LambdaQueryWrapper<ApartmentLabel> labelQueryWrapper = new LambdaQueryWrapper<>();
        labelQueryWrapper.eq(ApartmentLabel::getApartmentId,id);
        //4.删除杂费列表
        LambdaQueryWrapper<ApartmentFeeValue> feeQueryWrapper = new LambdaQueryWrapper<>();
        feeQueryWrapper.eq(ApartmentFeeValue::getApartmentId,id);
        apartmentFeeValueService.remove(feeQueryWrapper);
    }


}




