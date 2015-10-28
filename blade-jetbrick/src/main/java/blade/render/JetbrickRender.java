package blade.render;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.blade.Blade;
import com.blade.context.BladeWebContext;
import com.blade.http.Request;
import com.blade.render.ModelAndView;
import com.blade.render.Render;
import com.blade.servlet.Session;

import blade.kit.log.Logger;
import jetbrick.template.JetEngine;
import jetbrick.template.JetTemplate;

/**
 * Velocity渲染引擎
 * @author biezhi
 *
 */
public class JetbrickRender implements Render {
    
	private static final Logger LOGGER = Logger.getLogger(JetbrickRender.class);
			
	private JetEngine jetEngine;
    
	private Properties config;
	
	private Blade blade;
	/**
	 * 默认构造函数
	 */
	public JetbrickRender() {
		blade = Blade.me();
		config = new Properties();
		config.put("jetx.input.encoding", blade.encoding());
		config.put("jetx.output.encoding", blade.encoding());
		config.put("jetx.template.suffix", ".html");
		config.put("jetx.template.loaders", "jetbrick.template.loader.FileSystemResourceLoader");
		jetEngine = JetEngine.create(config);
	}
	
	/**
	 * @return	返回JetEngine引擎
	 */
	public JetEngine getJetEngine(){
		return jetEngine;
	}
	
	/**
	 * 清空配置
	 */
	public void clean(){
		if(null != config){
			config.clear();
		}
	}
	
	/**
	 * 添加一个配置
	 * @param key	配置键
	 * @param value	配置值
	 */
	public void put(String key, Object value){
		if(null == config){
			config = new Properties();
		}
		config.put(key, value);
	}
	
	/**
	 * 根据配置文件构造一个JetEngine引擎
	 * @param configLocation
	 * @throws IOException 
	 */
	public JetbrickRender(String configLocation) throws IOException {
		jetEngine = JetEngine.create(configLocation);
	}
	
	/**
	 * 根据构造一个JetEngine引擎
	 * @param config	Properties配置
	 */
	public JetbrickRender(Properties config) {
		this.config = config;
		jetEngine = JetEngine.create(this.config);
	}
	
	/**
	 * 手动构造JetEngine引擎
	 * @param jetEngine	jetEngine引擎
	 */
	public JetbrickRender(JetEngine jetEngine) {
		this.jetEngine = jetEngine;
	}
	

	@Override
	public void render(ModelAndView modelAndView, Writer writer) {
		Request request = BladeWebContext.request();
		Session session = request.session();
		
		JetTemplate template = jetEngine.getTemplate(modelAndView.getView());
		
		Map<String, Object> context = modelAndView.getModel();
		
		Set<String> attrs = request.attributes();
		if(null != attrs && attrs.size() > 0){
			for(String attr : attrs){
				context.put(attr, request.attribute(attr));
			}
		}
		
		Set<String> session_attrs = session.attributes();
		if(null != session_attrs && session_attrs.size() > 0){
			for(String attr : session_attrs){
				context.put(attr, session.attribute(attr));
			}
		}
		
		try {
			template.render(context, writer);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e);
		}
	}
	
}
