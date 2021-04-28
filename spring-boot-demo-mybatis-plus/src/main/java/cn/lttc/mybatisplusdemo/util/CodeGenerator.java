package cn.lttc.mybatisplusdemo.util;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 代码生成器
 *
 * @author YangShuangLing
 * @create 2021-04-19
 */
public class CodeGenerator {

    /**
     * 读取控制台内容
     * @param tip
     * @return
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        System.out.println(help.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotBlank(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    public static void main(String[] args) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        //region ========== 全局配置 ==========
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/spring-boot-demo-mybatisPlus/src/main/java"); //生成路径
        gc.setAuthor("yangshuangling"); //作者
        gc.setOpen(false); //是否打开输出目录
        // gc.setSwagger2(true); 实体属性 Swagger2 注解
        gc.setFileOverride(false); //文件覆盖
        gc.setMapperName("%sMapper");
        gc.setXmlName("%sMapper");
        gc.setServiceName("%sService"); //设置生成的service接口的名字的首字母是否为I,默认生成IProductService
        gc.setServiceImplName("%sServiceImpl");
        gc.setBaseResultMap(true); // mapper.xml中生成BaseResultMap
        gc.setActiveRecord(true); //是否支持AR模式
        gc.setBaseColumnList(true); //生成基本sql片段
        //gc.setIdType(IdType.ASSIGN_ID); 主键策略
        gc.setDateType(DateType.ONLY_DATE);//定义生成的实体类中日期类型,，默认LocalDateTime
        mpg.setGlobalConfig(gc);
        //endregion

        //region ========== 数据源配置 ==========
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.MYSQL); //设置数据库类型
        dsc.setUrl("jdbc:mysql://10.201.6.7:3306/demo?useUnicode=true&useSSL=false&characterEncoding=utf8");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("demo");
        dsc.setPassword("demo");
        mpg.setDataSource(dsc);
        //endregion

        //region ========== 包配置 ==========
        PackageConfig pc = new PackageConfig();
        pc.setModuleName(scanner("模块名"));
        pc.setParent("cn.lttc");
        mpg.setPackageInfo(pc);
        //endregion

        //region ========== 自定义配置（配置xml生成到resouces下） ==========
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };
        String templatePath = "/templates/mapper.xml.ftl"; // 如果模板引擎是 freemarker
        List<FileOutConfig> focList = new ArrayList<>(); //自定义输出
        focList.add(new FileOutConfig(templatePath) { // 自定义配置会被优先输出
                        @Override
                        public String outputFile(TableInfo tableInfo) {
                            // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                            return projectPath + "/spring-boot-demo-mybatisPlus/src/main/resources/mapper/" + pc.getModuleName()
                                    + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
                        }
                    }
        );
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);
        //endregion

        //region ========== 模板配置 ==========
        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        //endregion

        //region ========== 策略配置 ==========
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel); //数据库表映射到实体的命名策略
        strategy.setColumnNaming(NamingStrategy.underline_to_camel); //数据库字段映射到属性的命名策略
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        strategy.setTablePrefix("mp"); //表前缀
        //strategy.setTablePrefix(pc.getModuleName() + "_");
        strategy.setInclude(scanner("表名，多个英文逗号分割").split(",")); //表名
        strategy.setControllerMappingHyphenStyle(true);
        mpg.setStrategy(strategy);
        //endregion

        mpg.execute();
    }

}
