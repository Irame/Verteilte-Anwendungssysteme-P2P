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
        Uri url;
        string id;
        string icon;

        public string ID
        {
            get { return id; }
            set { id = value; OnPropertyChanged(); OnPropertyChanged(nameof(DisplayID)); }
        }

        public string IP
        {
            get { return url.Authority; }
        }

        public string DisplayID
        {
            get { return id.Substring(0, 8); }
        }

        public string Icon
        {
            get { return icon; }
            set { icon = value; OnPropertyChanged(); }
        }

        public System.Windows.Media.Color Color
        {
            get { return SmileySelector.Instance.GetColor(url.Host); }
        }

        public ChordNode(string id, string urlString)
        {
            this.id = id;
            this.url = new Uri(urlString);
            this.Icon = SmileySelector.Instance.GetSmiley(id);
        }

        protected bool Equals(ChordNode other)
        {
            return string.Equals(id, other.id);
        }

        public override bool Equals(object obj)
        {
            if (ReferenceEquals(null, obj)) return false;
            if (ReferenceEquals(this, obj)) return true;
            if (obj.GetType() != this.GetType()) return false;
            return Equals((ChordNode) obj);
        }

        public override int GetHashCode()
        {
            return (id != null ? id.GetHashCode() : 0);
        }
    }
}
