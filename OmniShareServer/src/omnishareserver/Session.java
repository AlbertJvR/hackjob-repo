package omnishareserver;

import java.util.ArrayList;

/**
 *
 * @author Skimmel
 */
public class Session
{
    private ArrayList<String> devicesConnected;
    private ArrayList<String> filesList;
    private boolean isActive;
    private boolean hasHost;
    private ArrayList<String> hosts;
    private String accessCode; //how to make this secure/enc
    private String meetingName;

    public Session()
    {
        devicesConnected = new ArrayList<>();
        filesList = new ArrayList<>();   
        isActive = true;
        hasHost = false;
        hosts = new ArrayList<>();
        meetingName = "";
    }

    public void setMeetingName(String name)
    {
        meetingName = name;
    }
    
    public String getMeetingName()
    {
        return meetingName;
    }
    
    public void setAccessCode(String accessCode)
    {
        this.accessCode = accessCode;
    }
    
    public boolean checkAccessCode(String accessCode)
    {
        if(accessCode.equals(this.accessCode))
        {
            return true;            
        }
        else            
        {
            return false;
        }
    }

    public void setActive(boolean isActive)
    {
        this.isActive = isActive;
    }

    public ArrayList<String> getDeviceList()
    {
        return devicesConnected;
    }
    
    
    
    public void addDevice(String dev)
    {
        if(!devicesConnected.contains(dev))
        {
            devicesConnected.add(dev);
        }
    }
    
    public void addFile(String file)
    {
        if(!filesList.contains(file))
        {
            filesList.add(file);
        }
    }
    
    public ArrayList<String> getFileList()
    {
        return filesList;
    }
    
    public boolean isActive()
    {
        return isActive;
    }
    
    public void disable()
    {
        isActive = false;
    }

    public boolean hasHost()
    {
        return hasHost;
    }
    
    
    
}
