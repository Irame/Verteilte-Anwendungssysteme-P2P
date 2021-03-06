﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NancyServerChord
{
    enum DataType
    {
        String, File
    }

    class EntryInfo
    {
        public string Person;
        public string KeyName;
        public DataType DataType;

        public void Normalize()
        {
            Person = Person?.Trim();
            KeyName = KeyName?.Trim();
        }
        
        public bool IsEmpty()
        {
            return string.IsNullOrEmpty(Person) || string.IsNullOrEmpty(KeyName);
        }
    }
}
