using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Runtime.CompilerServices;
using System.Text;
using System.Threading.Tasks;

namespace ChordDisplay
{
    public class ChordNode : PropertyChangedBase
    {
        string id;
        public string ID
        {
            get { return id; }
            set { id = value; OnPropertyChanged(); }
        }

        string ip;
        public string IP
        {
            get { return ip; }
            set { ip = value; OnPropertyChanged(); }
        }
    }
}
